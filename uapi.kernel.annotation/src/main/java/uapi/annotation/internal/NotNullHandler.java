package uapi.annotation.internal;

import com.google.auto.service.AutoService;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A Handler for handle NotNull annotation
 */
@AutoService(IAnnotationsHandler.class)
public final class NotNullHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { NotNull.class };

    @Override
    public Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    public void handleAnnotatedElements(
            final IBuilderContext builderCtx,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> paramElements
    ) throws KernelException {
        ArgumentChecker.equals(annotationType, NotNull.class, "annotationType");

        paramElements.forEach(paramElement -> {
            if (paramElement.getKind() != ElementKind.PARAMETER) {
                throw new KernelException(
                        "The NotNull annotation only can be applied on method parameter",
                        paramElement.getSimpleName().toString());
            }
            Element methodElement = paramElement.getEnclosingElement();
//            ArgumentChecker.notContains(
//                    methodElement.getModifiers(),
//                    methodElement.getSimpleName().toString(),
//                    "NotNull annotation",
//                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            Element classElement = methodElement.getEnclosingElement();
//            ArgumentChecker.notContains(
//                    classElement.getModifiers(),
//                    classElement.getSimpleName().toString(),
//                    "NotNull annotation",
//                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            checkModifiers(methodElement, NotNull.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            checkModifiers(classElement, NotNull.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElement);
            MethodMeta.Builder methodBuilder = clsBuilder.findMethodBuilder(methodElement, builderCtx);
            ParameterMeta.Builder paramBuilder = methodBuilder.findParameterBuilder(paramElement, builderCtx);

            String codes = StringHelper.makeString(
                    "{}.notNull({}, \"{}\");\n",
                    ArgumentChecker.class.getName(),
                    paramBuilder.getName(),
                    paramBuilder.getName());

            methodBuilder
                    .addThrowTypeName(InvalidArgumentException.class.getName())
                    .setInvokeSuper(MethodMeta.InvokeSuper.AFTER)
                    .addCodeBuilder(CodeMeta.builder().addRawCode(codes));
        });
    }
}
