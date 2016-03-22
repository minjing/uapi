package uapi.service.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import rx.Observable;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.StringHelper;
import uapi.service.IService;
import uapi.service.IServiceFactory;
import uapi.service.SetterMeta;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;
import uapi.service.annotation.Service;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A annotation handler used to handler Service annotation
 */
@AutoService(IAnnotationsHandler.class)
public final class ServiceHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { Service.class };

    private static final String TEMPLATE_GET_IDS            = "template/getIds_method.ftl";

    @Override
    protected Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    protected void handleAnnotatedElements(
            final IBuilderContext builderCtx,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException {
        elements.forEach(classElement -> {
            if (classElement.getKind() != ElementKind.CLASS) {
                throw new KernelException(
                        "The Service annotation only can be applied on class",
                        classElement.getSimpleName().toString());
            }
            builderCtx.checkModifiers(classElement, Service.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            // Receive service id array
            ClassMeta.Builder classBuilder = builderCtx.findClassBuilder(classElement);
            Service service = classElement.getAnnotation(Service.class);
            String[] serviceIds = service.value();
            if (serviceIds == null || serviceIds.length == 0) {
                final StringBuilder svcId = new StringBuilder();
                // Check service factory type argument first
                Observable.from(((TypeElement) classElement).getInterfaces())
                        .filter(declareType -> declareType.toString().startsWith(IServiceFactory.class.getName()))
                        .map(declareType -> ((DeclaredType) declareType).getTypeArguments().get(0))
                        .subscribe(svcId::append);
                if (svcId.length() == 0) {
                    // If the service is not a factory, using service class type
                    svcId.append(StringHelper.makeString("{}.{}",
                            classBuilder.getPackageName(), classElement.getSimpleName().toString()));
                }
                serviceIds = new String[] { svcId.toString() };
            }
            Template tempGetIds = builderCtx.loadTemplate(TEMPLATE_GET_IDS);
            Map<String, Object> tempModelInit = new HashMap<>();
            tempModelInit.put("serviceIds", serviceIds);

            // Build class builder
            classBuilder
                    .addAnnotationBuilder(AnnotationMeta.builder()
                            .setName(AutoService.class.getCanonicalName())
                            .addArgument(ArgumentMeta.builder()
                                    .setName("value")
                                    .setIsString(false)
                                    .setValue(IService.class.getCanonicalName() + ".class")))
                    .addImplement(IService.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder()
                                    .setName(AnnotationMeta.OVERRIDE))
                            .setName(IService.METHOD_GETIDS)
                            .addModifier(Modifier.PUBLIC)
                            .setReturnTypeName(IService.METHOD_GETIDS_RETURN_TYPE)
                            .addCodeBuilder(CodeMeta.builder()
                                    .setTemplate(tempGetIds)
                                    .setModel(tempModelInit)));
        });
    }
}
