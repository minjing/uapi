package uapi.service.internal;

import com.google.common.base.Strings;
import freemarker.template.Template;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ArgumentChecker;
import uapi.helper.ClassHelper;
import uapi.helper.StringHelper;
import uapi.service.IInjectable;
import uapi.service.IService;
import uapi.service.Injection;
import uapi.service.SetterMeta;
import uapi.service.annotation.Inject;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The handler for Inject annotation
 */
public class InjectParser {

    private static final String TEMPLATE_FILE               = "template/inject_method.ftl";
    private static final String TEMPLATE_GET_DEPENDENT_IDS  = "template/getDependentIds_method.ftl";
    private static final String SETTER_PARAM_NAME           = "value";

    public void parse(
            final IBuilderContext builderCtx,
            final Set<? extends Element> elements
    ) throws KernelException {
//        Set<? extends Element> paramElements = builderCtx.getElementsAnnotatedWith(Inject.class);
//        if (paramElements.size() == 0) {
//            return;
//        }

        elements.forEach(fieldElement -> {
            if (fieldElement.getKind() != ElementKind.FIELD) {
                throw new KernelException(
                        "The Inject annotation only can be applied on field",
                        fieldElement.getSimpleName().toString());
            }
            builderCtx.checkModifiers(fieldElement, Inject.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            Element classElemt = fieldElement.getEnclosingElement();
            builderCtx.checkModifiers(classElemt, Inject.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

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
                injectId = fieldTypeName;
            }

            String paramName = SETTER_PARAM_NAME;
            String code;
            if (isCollection) {
                code = StringHelper.makeString("{}.add({});", fieldName, paramName);
            } else {
                code = StringHelper.makeString("{}={};", fieldName, paramName);
            }

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            clsBuilder
                    .addImplement(IInjectable.class.getCanonicalName())
                    .addMethodBuilder(SetterMeta.builder()
                            .setFieldName(fieldName)
                            .setInjectId(injectId)
                            .setInjectType(fieldTypeName)
                            .setName(setterName)
                            .setReturnTypeName(Type.VOID)
                            .setInvokeSuper(MethodMeta.InvokeSuper.NONE)
                            .addParameterBuilder(ParameterMeta.builder()
                                    .addModifier(Modifier.FINAL)
                                    .setName(paramName)
                                    .setType(fieldTypeName))
                            .addCodeBuilder(CodeMeta.builder()
                                    .addRawCode(code)));
        });

        implementIInjectable(builderCtx);
        implementGetDependencyIds(builderCtx);
    }

    private boolean isCollection(
            final Element fieldElement,
            final IBuilderContext builderCtx) {
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

    private void implementGetDependencyIds(
            final IBuilderContext builderCtx
    ) throws KernelException {
        builderCtx.getBuilders().forEach(classBuilder -> {
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
                        }
                    });
            Template tempDependentIds = builderCtx.loadTemplate(TEMPLATE_GET_DEPENDENT_IDS);
            Map<String, Object> tempModelDependentIds = new HashMap<>();
            tempModelDependentIds.put("dependentIds", dependentIds);

            classBuilder.addMethodBuilder(MethodMeta.builder()
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

    private void implementIInjectable(
            final IBuilderContext builderContext
    ) throws KernelException {
        Template temp = builderContext.loadTemplate(TEMPLATE_FILE);

        String methodName = "injectObject";
        String paramName = "injection";
        String paramType = Injection.class.getName();
        builderContext.getBuilders().forEach(classBuilder -> {
            final List<SetterModel> setterModels = new ArrayList<>();
            classBuilder.findSetterBuilders().forEach(methodBuilder -> {
                SetterMeta.Builder setterBuilder = (SetterMeta.Builder) methodBuilder;
                setterModels.add(new SetterModel(
                        setterBuilder.getName(),
                        setterBuilder.getInjectId(),
                        setterBuilder.getInjectType()));
            });
//            if (setterModels.size() >= 0) {
            Map<String, Object> tempModel = new HashMap<>();
            tempModel.put("setters", setterModels);

            classBuilder
                    .addImplement(IInjectable.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder()
                                    .setName("Override"))
                            .addModifier(Modifier.PUBLIC)
                            .setName(methodName)
                            .setReturnTypeName(Type.VOID)
                            .addThrowTypeName(InvalidArgumentException.class.getCanonicalName())
                            .addParameterBuilder(ParameterMeta.builder()
                                    .addModifier(Modifier.FINAL)
                                    .setName(paramName)
                                    .setType(paramType))
                            .addCodeBuilder(CodeMeta.builder()
                                    .setModel(tempModel)
                                    .setTemplate(temp)));
//            }
        });
    }

    public static final class SetterModel {

        private String _name;
        private String _injectId;
        private String _injectType;

        private SetterModel(
                final String name,
                final String injectId,
                final String injectType
        ) throws InvalidArgumentException {
            ArgumentChecker.notEmpty(name, "name");
            ArgumentChecker.notEmpty(injectId, "injectId");
            ArgumentChecker.notEmpty(injectType, "injectType");
            this._name = name;
            this._injectId = injectId;
            this._injectType = injectType;
        }

        public String getName() {
            return this._name;
        }

        public String getInjectId() {
            return this._injectId;
        }

        public String getInjectType() {
            return this._injectType;
        }
    }
}
