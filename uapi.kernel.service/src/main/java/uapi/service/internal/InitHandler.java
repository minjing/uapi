package uapi.service.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.StringHelper;
import uapi.service.IInitial;
import uapi.service.IService;
import uapi.service.annotation.Init;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Set;

/**
 * A handler used to handle Init annotation
 */
@AutoService(AnnotationHandler.class)
public final class InitHandler extends AnnotationHandler<Init> {

    private static final String METHOD_INIT_NAME    = "init";

    @Override
    public Class<Init> getSupportAnnotationType() {
        return Init.class;
    }

    @Override
    public void handle(
            final RoundEnvironment roundEnv,
            final IBuilderContext buildCtx
    ) throws KernelException {
        Set<? extends Element> methodElements = roundEnv.getElementsAnnotatedWith(Init.class);
        if (methodElements.size() == 0) {
            return;
        }

        methodElements.forEach(methodElement -> {
            if (methodElement.getKind() != ElementKind.METHOD) {
                throw new KernelException(
                        "The Init annotation only can be applied on method",
                        methodElement.getSimpleName().toString());
            }
            checkModifiers(methodElement, Modifier.PRIVATE, Modifier.STATIC);
            Element classElemt = methodElement.getEnclosingElement();
            checkModifiers(classElemt, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            MethodMeta.Builder methodBuilder = MethodMeta.builder(methodElement, buildCtx);
            if (methodBuilder.getParameterCount() > 0) {
                throw new KernelException(
                        "The method [{}:{}] with Init annotation can not has any parameter",
                        classElemt.getSimpleName().toString(),
                        methodElement.getSimpleName().toString());
            }

            String methodName = methodElement.getSimpleName().toString();
            ClassMeta.Builder clsBuilder = buildCtx.findClassBuilder(classElemt);
            List<MethodMeta.Builder> exising = clsBuilder.findMethodBuilders(METHOD_INIT_NAME);
            if (exising.size() > 0) {
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
