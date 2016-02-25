package uapi.annotation.internal;

import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * Created by min on 16/2/10.
 */
public final class NotNullHandler extends AnnotationHandler<NotNull> {

    @Override
    public Class<NotNull> getSupportAnnotationType() {
        return NotNull.class;
    }

    @Override
    public void handle(
            final RoundEnvironment roundEnv,
            final IBuilderContext builderCtx
    ) throws KernelException {
        Set<? extends Element> paramElements = roundEnv.getElementsAnnotatedWith(NotNull.class);
        if (paramElements.size() == 0) {
            return;
        }
        paramElements.forEach(paramElement -> {
            if (paramElement.getKind() != ElementKind.PARAMETER) {
                throw new KernelException(
                        "The NotNull annotation only can be applied on method parameter",
                        paramElement.getSimpleName().toString());
            }
            Element methodElement = paramElement.getEnclosingElement();
            Element classElement = methodElement.getEnclosingElement();
            checkModifiers(methodElement, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            checkModifiers(classElement, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElement);
            MethodMeta.Builder methodBuilder = clsBuilder.findMethodBuilder(methodElement, builderCtx);
            ParameterMeta.Builder paramBuilder = methodBuilder.findParameterBuilder(paramElement, builderCtx);

            String codes = StringHelper.makeString(
                    "uapi.helper.ArgumentChecker.notNull({}, \"{}\");\n",
                    paramBuilder.getName(), paramBuilder.getName());

            methodBuilder
                    .addThrowTypeName("uapi.InvalidArgumentException")
                    .setInvokeSuper(MethodMeta.InvokeSuper.AFTER)
                    .addCodeBuilder(CodeMeta.builder().addRawCode(codes));
        });
    }
}
