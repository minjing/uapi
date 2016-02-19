package uapi.injector.annotation;

import com.google.common.base.Strings;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ClassHelper;
import uapi.helper.StringHelper;
import uapi.injector.IInjectable;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by min on 16/2/16.
 */
public class InjectHandler extends AnnotationHandler<Inject> {

    private static final String SETTER_PARAM_NAME   = "value";

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
//        List<ClassMeta.Builder> svcClasses = new ArrayList<>();
        // setter name to inject id mapping
        Map<String, String> setterInjectIdMapper = new HashMap<>();
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
                fieldTypeName = typeArgs.get(0);
            }

            Inject inject = fieldElement.getAnnotation(Inject.class);
            String injectId = inject.value();
            if (Strings.isNullOrEmpty(injectId)) {
                injectId = fieldName;
            }


            String paramName = SETTER_PARAM_NAME;
            String code;
            if (isCollection) {
                code = StringHelper.makeString("{}.add({});", fieldName, paramName);
            } else {
                code = StringHelper.makeString("{}={};", fieldName, paramName);
            }

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            clsBuilder.addMethodBuilder(MethodMeta.builder()
                    .setName(setterName)
                    .setReturnTypeName(MethodMeta.TYPE_VOID)
                    .setInvokeSuper(MethodMeta.InvokeSuper.NONE)
                    .setIsSetter(true)
                    .addParameterBuilder(ParameterMeta.builder()
                            .setName(paramName)
                            .setType(fieldTypeName))
                    .addCodes(code));
//            svcClasses.add(clsBuilder);
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

    /**
     * Generate inject method code like below:
     * {@code
     *      if (injection.getId().equals(xxx)) {
     *          injection.checkType(xxx.class);
     *          setter(injection.getObject());
     *      } else if (xxx) {
     *          ....
     *      } else {
     *          throw new KernelException("Can't inject object {} into service {}", injection, this);
     *      }
     * }
     *
     * @return code
     */
    private void implementIInjectable(List<ClassMeta.Builder> classBuilders) {
        String methodName = "injectObject";
        String paramName = "injection";
        String paramType = "uapi.injector.Injection";
        classBuilders.forEach(classBuilder -> {
            StringBuffer codeBuilder = new StringBuffer();
            AtomicBoolean hasIf = new AtomicBoolean(false);
            classBuilder.findSetterBuilder().forEach(setterBuilder -> {
                ParameterMeta.Builder param = setterBuilder.findParameterBuilder(SETTER_PARAM_NAME);
                if (! hasIf.get()) {
                    codeBuilder.append(StringHelper.makeString(
                            "if (injection.getId().equals({})) {\n" +
                                    "injection.checkType({}.class);\n" +
                                    "{}(({}) injection.getObject())",
                            "serviceId", param.getType(), setterBuilder.getName(), param.getType()));
                    hasIf.set(true);
                } else {

                }
            });
            if (hasIf.get()) {
                codeBuilder.append(StringHelper.makeString(
                        " else {\n" +
                                "throw new uapi.KernelException(\"Can't inject object {} into service {}, injection, this);\n" +
                                "}"));
            } else {

            }
            classBuilder.addImplement(IInjectable.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addModifier(Modifier.PUBLIC)
                            .setName(methodName)
                            .setReturnTypeName(MethodMeta.TYPE_VOID)
                            .addParameterBuilder(ParameterMeta.builder()
                                    .addModifier(Modifier.FINAL)
                                    .setName(paramName)
                                    .setType(paramType))
                            .addCodes(codeBuilder.toString()));
        });
    }
}
