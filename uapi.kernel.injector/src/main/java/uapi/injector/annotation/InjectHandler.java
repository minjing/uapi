package uapi.injector.annotation;

import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ClassHelper;
import uapi.helper.StringHelper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by min on 16/2/16.
 */
public class InjectHandler extends AnnotationHandler<Inject> {

    @Override
    public Class<Inject> getSupportAnnotationType() {
        return Inject.class;
    }

    @Override
    public void handle(
            final RoundEnvironment roundEnv,
            final BuilderContext builderCtx
    ) throws KernelException {
        getLogger().info("IN InjectHandler!!!!");
        Set<? extends Element> paramElements = roundEnv.getElementsAnnotatedWith(Inject.class);
        if (paramElements.size() == 0) {
            return;
        }
        paramElements.forEach(fieldElement -> {
            if (fieldElement.getKind() != ElementKind.FIELD) {
                throw new KernelException(
                        "The Inject annotation only can be applied on field",
                        fieldElement.getSimpleName().toString());
            }
            checkModifiers(fieldElement, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            Element classElemt = fieldElement.getEnclosingElement();
            checkModifiers(classElemt, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            String fieldName = fieldElement.getSimpleName().toString();
            String fieldTypeName = fieldElement.asType().toString();
            boolean isCollection = isCollection(fieldElement, builderCtx);
            String setterName = ClassHelper.makeSetterName(fieldName, isCollection);
            if (isCollection) {
                List<String> typeArgs = getTypeArguments(fieldElement);
                if (typeArgs.size() != 1) {
                    throw new KernelException(
                            "The collection field [{}.{}] must be define only ONE type argument",
                            classElemt.getSimpleName().toString(),
                            fieldElement.getSimpleName().toString());
                }
                fieldTypeName = typeArgs.get(0).toString();
            }

            String paramName = "value";
            String code;
            if (isCollection) {
                code = StringHelper.makeString("{}.add({});", fieldName, paramName);
            } else {
                code = StringHelper.makeString("{}={};", fieldName, paramName);
            }

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            clsBuilder.addMethodBuilder(MethodMeta.builder()
                    .setName(setterName).setReturnTypeName("void")
                    .setIsProperty(true)
                    .addParameterBuilder(ParameterMeta.builder()
                            .setName(paramName)
                            .setType(fieldTypeName))
                    .addCodes(code));
        });
    }

    private boolean isCollection(Element fieldElement, BuilderContext builderCtx) {
        Elements elemtUtils = builderCtx.getElementUtils();
        Types typeUtils = builderCtx.getTypeUtils();
        WildcardType wildcardType = typeUtils.getWildcardType(null, null);
        TypeElement collectionTypeElemt = elemtUtils.getTypeElement(
                Collection.class.getCanonicalName());
        DeclaredType collectionType = typeUtils.getDeclaredType(
                collectionTypeElemt, wildcardType);
        return typeUtils.isAssignable(fieldElement.asType(), collectionType);
    }

    private List<String> getTypeArguments(Element fieldElement) {
        final List<String> typeArgs = new ArrayList<>();
        DeclaredType declaredType = (DeclaredType) fieldElement.asType();
        declaredType.getTypeArguments().forEach(
                typeMirror -> typeArgs.add(typeMirror.toString()));
        return typeArgs;
    }
}
