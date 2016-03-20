package uapi.service.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.service.IInitial;
import uapi.service.annotation.Init;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * A handler used to handle IInitial related annotations
 */
@AutoService(IAnnotationsHandler.class)
public final class InitialHandler extends AnnotationsHandler {

    private static final String METHOD_INIT_NAME    = "init";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { Init.class };

    @Override
    public Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    public void handleAnnotatedElements(
            final IBuilderContext builderCtx,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> methodElements
    ) throws KernelException {
        ArgumentChecker.equals(annotationType, Init.class, "annotationType");

        getLogger().info("Start processing Init annotation");
        methodElements.forEach(methodElement -> {
            if (methodElement.getKind() != ElementKind.METHOD) {
                throw new KernelException(
                        "The Init annotation only can be applied on method",
                        methodElement.getSimpleName().toString());
            }
            checkModifiers(methodElement, Init.class, Modifier.PRIVATE, Modifier.STATIC);
            Element classElemt = methodElement.getEnclosingElement();
            checkModifiers(classElemt, Init.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            MethodMeta.Builder methodBuilder = MethodMeta.builder(methodElement, builderCtx);
            if (methodBuilder.getParameterCount() > 0) {
                throw new KernelException(
                        "The method [{}:{}] with Init annotation can not has any parameter",
                        classElemt.getSimpleName().toString(),
                        methodElement.getSimpleName().toString());
            }

            String methodName = methodElement.getSimpleName().toString();
            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            List<MethodMeta.Builder> existing = clsBuilder.findMethodBuilders(METHOD_INIT_NAME);
            if (existing.size() > 0) {
                throw new KernelException(
                        "Multiple Init annotation was defined in the class {}",
                        classElemt.getSimpleName().toString());
            }
            String code = StringHelper.makeString(
                    "super.{}();", methodName);
            clsBuilder
                    .addImplement(IInitial.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addModifier(Modifier.PUBLIC)
                            .setName(METHOD_INIT_NAME)
                            .addAnnotationBuilder(AnnotationMeta.builder()
                                    .setName("Override"))
                            .addCodeBuilder(CodeMeta.builder()
                                    .addRawCode(code))
                            .setReturnTypeName("void"));
        });
    }
}
