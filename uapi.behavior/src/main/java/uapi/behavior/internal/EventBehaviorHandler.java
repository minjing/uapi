package uapi.behavior.internal;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import freemarker.template.Template;
import uapi.KernelException;
import uapi.Type;
import uapi.annotation.*;
import uapi.behavior.IEventDrivenBehavior;
import uapi.behavior.IExecutable;
import uapi.behavior.IExecution;
import uapi.behavior.IExecutionContext;
import uapi.behavior.annotation.EventBehavior;
import uapi.event.IEvent;
import uapi.event.IEventBus;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.IInitialHandlerHelper;
import uapi.service.IInjectableHandlerHelper;
import uapi.service.internal.QualifiedServiceId;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The annotation handler will generate class which implement IEventDrivenBehavior interface
 */
@AutoService(IAnnotationsHandler.class)
public class EventBehaviorHandler extends AnnotationsHandler {

    private static final String TEMPLATE_PROCESS        = "template/process_method.ftl";
    private static final String TEMPLATE_HANDLE         = "template/handle_method.ftl";

    private static final String METHOD_EXECTUION_INIT   = "__initExecution";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] {
            EventBehavior.class
    };

    @Override
    protected Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    protected void handleAnnotatedElements(
            final IBuilderContext builderContext,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException {
        ArgumentChecker.notNull(annotationType, "annotationType");

        Looper.from(elements).foreach(classElement -> {
            if (classElement.getKind() != ElementKind.CLASS) {
                throw new KernelException(
                        "The EventBehavior annotation only can be applied on class",
                        classElement.getSimpleName().toString());
            }
            // Check the class must be implement IExecutable interface
            if (! builderContext.isAssignable(classElement, IExecutable.class)) {
                throw new KernelException(
                        "The class {} must be implement IExecutable interface",
                        classElement.getSimpleName().toString());
            }

            // Check annotation
            EventBehavior eventBehavior = classElement.getAnnotation(EventBehavior.class);
            String name = eventBehavior.name();
            String topic = eventBehavior.topic();
            AnnotationMirror ebAnnoMirror = MoreElements.getAnnotationMirror(classElement, EventBehavior.class).get();
            String eventType = getTypeInAnnotation(ebAnnoMirror, "event", builderContext.getLogger());
            if (eventType == null) {
                throw new KernelException(
                        "Can't read event property from EventBehavior annotation on class - {}",
                        classElement.getSimpleName().toString());
            }
            if (Strings.isNullOrEmpty(name)) {
                throw new KernelException("The name property is required for EventBehavior annotation");
            }
            if (Strings.isNullOrEmpty(topic)) {
                throw new KernelException("The topic property is required for EventBehavior annotation");
            }
            if (! builderContext.isAssignable(eventType, IEvent.class)) {
                throw new KernelException(
                        "The event property {} of EventBehavior annotation on {} must be implement IEvent interface",
                        eventType, classElement.getSimpleName().toString());
            }

            // Build model
            Map<String, String> model = new HashMap<>();
            model.put("inputType", eventType);
            model.put("executionField", "_execution");

            Template tempProcess = builderContext.loadTemplate(TEMPLATE_PROCESS);
            Template tempHandle = builderContext.loadTemplate(TEMPLATE_HANDLE);

            ClassMeta.Builder clsBuilder = builderContext.findClassBuilder(classElement);
            clsBuilder.addImplement(StringHelper.makeString("{}<{}>", IEventDrivenBehavior.class.getCanonicalName(), eventType))
                    .addFieldBuilder(FieldMeta.builder()
                            .setName(model.get("executionField"))
                            .setTypeName(IExecution.class.getCanonicalName())
                            .addModifier(Modifier.PRIVATE))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("name")
                            .addModifier(Modifier.PUBLIC)
                            .setReturnTypeName(Type.Q_STRING)
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("return \"{}\";", name))))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("topic")
                            .addModifier(Modifier.PUBLIC)
                            .setReturnTypeName(Type.Q_STRING)
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("return \"{}\";", topic))))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("inputType")
                            .addModifier(Modifier.PUBLIC)
                            .setReturnTypeName(StringHelper.makeString("Class<{}>", eventType))
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("return {}.class;", eventType))))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("process")
                            .addModifier(Modifier.PUBLIC)
                            .addParameterBuilder(ParameterMeta.builder().setName("input").setType(eventType))
                            .addParameterBuilder(ParameterMeta.builder().setName("context").setType(IExecutionContext.class.getCanonicalName()))
                            .setReturnTypeName(Void.class.getCanonicalName())
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .addCodeBuilder(CodeMeta.builder().setTemplate(tempProcess).setModel(model)))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("handle")
                            .addModifier(Modifier.PUBLIC)
                            .addParameterBuilder(ParameterMeta.builder().setName("event").setType(eventType))
                            .setReturnTypeName(Type.VOID)
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .addCodeBuilder(CodeMeta.builder().setTemplate(tempHandle).setModel(model)))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName(METHOD_EXECTUION_INIT)
                            .addModifier(Modifier.PROTECTED)
                            .setReturnTypeName(Type.VOID)
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("this._execution = super.execution();"))));

            IInjectableHandlerHelper injectHelper = (IInjectableHandlerHelper) builderContext.getHelper(IInjectableHandlerHelper.name);
            injectHelper.addDependency(
                    builderContext,
                    clsBuilder,
                    "_eventBus",
                    IEventBus.class.getCanonicalName(),
                    IEventBus.class.getCanonicalName(),
                    QualifiedServiceId.FROM_LOCAL,
                    false, false, null, false);

            IInitialHandlerHelper initialHelper = (IInitialHandlerHelper) builderContext.getHelper(IInitialHandlerHelper.name);
            initialHelper.addInitMethod(builderContext, clsBuilder, METHOD_EXECTUION_INIT);
        });
    }
}
