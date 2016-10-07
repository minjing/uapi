package uapi.event.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.annotation.AnnotationMeta;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.ClassMeta;
import uapi.annotation.CodeMeta;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.annotation.MethodMeta;
import uapi.event.IEventHandler;
import uapi.event.annotation.EventHandler;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.IServiceHandlerHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A annotation handler for Event annotation
 */
@AutoService(IAnnotationsHandler.class)
public class EventHandlerAnnotationHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] { EventHandler.class };

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
        ArgumentChecker.equals(annotationType, EventHandler.class, "annotationType");

        Looper.from(elements)
                .foreach(element -> {
                    if (element.getKind() != ElementKind.CLASS) {
                        throw new KernelException(
                                "The EventHandler annotation only can be applied on class element - {}",
                                element.getSimpleName().toString());
                    }

                    checkModifiers(element, EventHandler.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

                    ClassMeta.Builder clsBuilder = builderContext.findClassBuilder(element);
                    // Make the event handler must be a IService instance
                    IServiceHandlerHelper svcHandlerHelper = (IServiceHandlerHelper) builderContext.getHelper(IServiceHandlerHelper.name);
                    svcHandlerHelper.addServiceId(clsBuilder, IEventHandler.class.getCanonicalName());

                    String topic = element.getAnnotation(EventHandler.class).value();
                    String codes = StringHelper.makeString("return {};", topic);
                    clsBuilder
                            .addImplement(IEventHandler.class.getCanonicalName())
                            .addMethodBuilder(MethodMeta.builder()
                                    .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                                    .addModifier(Modifier.PUBLIC)
                                    .setName("topic")
                                    .setReturnTypeName(String.class.getCanonicalName())
                                    .addCodeBuilder(CodeMeta.builder().addRawCode(codes))
                            );
                });
    }
}
