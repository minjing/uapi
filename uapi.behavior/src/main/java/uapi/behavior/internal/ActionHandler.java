package uapi.behavior.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.Type;
import uapi.annotation.*;
import uapi.behavior.annotation.Action;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.annotation.Service;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * The handler is used to handle IAction related annotations
 */
@AutoService(IAnnotationsHandler.class)
public class ActionHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] { Action.class };

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
        if (annotationType != Action.class) {
            throw new KernelException("Unsupported annotation type - {}", annotationType.getCanonicalName());
        }

        Looper.from(elements).foreach(classElement -> {
            if (classElement.getKind() != ElementKind.CLASS) {
                throw new KernelException(
                        "The element {} must be a class element", classElement.getSimpleName().toString());
            }
            builderContext.checkAnnotations(classElement, Service.class);

            TypeElement typeElement = (TypeElement) classElement;
            List<TypeMirror> intfTypes = (List<TypeMirror>) typeElement.getInterfaces();
            DeclaredType actionType = Looper.from(intfTypes)
                    .filter(intfType -> intfType instanceof DeclaredType)
                    .map(intfType -> (DeclaredType) intfType)
                    .first();
            if (actionType == null) {
                throw new KernelException(
                        "The action class {} must implement IAction interface and specified its parameterized types",
                        classElement.getSimpleName().toString());
            }
            List typeArgs = actionType.getTypeArguments();
            if (typeArgs.size() != 2) {
                throw new KernelException(
                        "The parameterized types of IAction must be 2 - {}", classElement.getSimpleName().toString());
            }

            Action action = classElement.getAnnotation(Action.class);
            String actionName = action.value();
            String inputType = typeArgs.get(0).toString(); //(DeclaredType) typeArgs.get(0)).asElement().getSimpleName().toString();
            String outputType = typeArgs.get(1).toString(); //((DeclaredType) typeArgs.get(1)).asElement().getSimpleName().toString();

            ClassMeta.Builder clsBuilder = builderContext.findClassBuilder(classElement);
            clsBuilder
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("name")
                            .addModifier(Modifier.PUBLIC)
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .setReturnTypeName(Type.STRING)
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("return \"{}\";", actionName))))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("inputType")
                            .addModifier(Modifier.PUBLIC)
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .setReturnTypeName(StringHelper.makeString("java.lang.Class<{}>", inputType))
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("return {}.class;", inputType))))
                    .addMethodBuilder(MethodMeta.builder()
                            .setName("outputType")
                            .addModifier(Modifier.PUBLIC)
                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                            .setReturnTypeName(StringHelper.makeString("java.lang.Class<{}>", outputType))
                            .addCodeBuilder(CodeMeta.builder().addRawCode(StringHelper.makeString("return {}.class;", outputType))));
        });
    }
}
