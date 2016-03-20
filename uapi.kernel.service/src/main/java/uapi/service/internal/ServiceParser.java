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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A annotation handler used to handler Service annotation
 */
public final class ServiceParser {

    private static final String TEMPLATE_GET_IDS            = "template/getIds_method.ftl";
    private static final String TEMPLATE_GET_DEPENDENT_IDS  = "template/getDependentIds_method.ftl";

//    @Override
//    public Class<Service> getSupportAnnotationType() {
//        return Service.class;
//    }
//
//    @Override
//    public Class[] afterHandledAnnotations() {
//        return new Class[] { Inject.class, Optional.class };
//    }

//    @Override
    public void parse(
            final IBuilderContext builderCtx
    ) throws KernelException {
        Set<? extends Element> classElements = builderCtx.getElementsAnnotatedWith(Service.class);
        if (classElements.size() == 0) {
            throw new KernelException("The service must be declared with Service annotation.");
        }

        classElements.forEach(classElement -> {
            if (classElement.getKind() != ElementKind.CLASS) {
                throw new KernelException(
                        "The Service annotation only can be applied on class",
                        classElement.getSimpleName().toString());
            }
            checkModifiers(classElement, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

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

            // Receive service dependency id list
            List<MethodMeta.Builder> setterBuilders = classBuilder.findSetterBuilders();
            List<String> dependentIds = setterBuilders.parallelStream()
                    .map(setterBuilder -> ((SetterMeta.Builder) setterBuilder).getInjectId())
                    .collect(Collectors.toList());
            // Check duplicated dependency
            dependentIds.stream()
                    .collect(Collectors.groupingBy(p -> p, Collectors.summingInt(p -> 1)))
                    .forEach((dependSvc, counter) -> {
                        if (counter > 1) {
                            throw new KernelException(StringHelper.makeString(
                                    "The service {}.{} has duplicated dependency on same service {}",
                                    classBuilder.getPackageName(),
                                    classBuilder.getClassName(),
                                    dependSvc));
                        }});
            Template tempDependentIds = builderCtx.loadTemplate(TEMPLATE_GET_DEPENDENT_IDS);
            Map<String, Object> tempModelDependentIds = new HashMap<>();
            tempModelDependentIds.put("dependentIds", dependentIds);

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
                                    .setModel(tempModelInit)))
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder()
                                    .setName(AnnotationMeta.OVERRIDE))
                            .setName(IService.METHOD_GET_DEPENDENT_ID)
                            .addModifier(Modifier.PUBLIC)
                            .setReturnTypeName(IService.METHOD_GET_DEPENDENT_ID_RETURN_TYPE)
                            .addCodeBuilder(CodeMeta.builder()
                                    .setTemplate(tempDependentIds)
                                    .setModel(tempModelDependentIds)));
        });
    }
}