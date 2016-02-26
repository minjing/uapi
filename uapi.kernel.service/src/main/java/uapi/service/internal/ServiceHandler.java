package uapi.service.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.StringHelper;
import uapi.service.IService;
import uapi.service.annotation.Service;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A annotation handler used to handler Service annotation
 */
@AutoService(AnnotationHandler.class)
public final class ServiceHandler extends AnnotationHandler<Service> {

    private static final String TEMPLATE_FILE = "template/init_method.ftl";

    @Override
    public Class<Service> getSupportAnnotationType() {
        return Service.class;
    }

    @Override
    public void handle(
            final IBuilderContext builderCtx
    ) throws KernelException {
        Set<? extends Element> classElements = builderCtx.getElementsAnnotatedWith(Service.class);
        if (classElements.size() == 0) {
            return;
        }

        classElements.forEach(classElement -> {
            if (classElement.getKind() != ElementKind.CLASS) {
                throw new KernelException(
                        "The Service annotation only can be applied on class",
                        classElement.getSimpleName().toString());
            }
            checkModifiers(classElement, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            ClassMeta.Builder classBuilder = builderCtx.findClassBuilder(classElement);
            Service service = classElement.getAnnotation(Service.class);
            String[] serviceIds = service.value();
            if (serviceIds == null || serviceIds.length == 0) {
                serviceIds = new String[] {
                        StringHelper.makeString(
                                "{}.{}",
                                classBuilder.getPackageName(),
                                classElement.getSimpleName().toString())};
            }

            Template temp = builderCtx.loadTemplate(TEMPLATE_FILE);
            Map<String, Object> tempModel = new HashMap<>();
            tempModel.put("serviceIds", serviceIds);
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
                                    .setTemplate(temp)
                                    .setModel(tempModel)));
        });
    }
}
