/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import com.google.common.base.Strings;
import freemarker.template.Template;
import rx.Observable;
import uapi.IIdentifiable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.Type;
import uapi.annotation.*;
import uapi.annotation.internal.BuilderContext;
import uapi.helper.ArgumentChecker;
import uapi.helper.ClassHelper;
import uapi.helper.Pair;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.*;
import uapi.service.annotation.Inject;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The handler for Inject annotation
 */
class InjectParser {

    private static final String TEMPLATE_FILE               = "template/inject_method.ftl";
    private static final String TEMPLATE_GET_DEPENDENCIES   = "template/getDependencies_method.ftl";
    private static final String SETTER_PARAM_NAME           = "value";

    private static final String MODEL_GET_DEPENDENCIES      = "ModelGetDependencies";
    private static final String VAR_DEPENDENCIES            = "dependencies";

    private final InjectParserHelper _helper = new InjectParserHelper();

    InjectParserHelper getHelper() {
        return this._helper;
    }

    public void parse(
            final IBuilderContext builderCtx,
            final Set<? extends Element> elements
    ) throws KernelException {
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
            boolean isMap = isMap(fieldElement, builderCtx);
            String setterName = ClassHelper.makeSetterName(fieldName, isCollection, isMap);
            String idType = null;
            if (isCollection) {
                List<TypeMirror> typeArgs = getTypeArguments(fieldElement);
                if (typeArgs.size() != 1) {
                    throw new KernelException(
                            "The collection field [{}.{}] must be define only ONE type argument",
                            classElemt.getSimpleName().toString(),
                            fieldElement.getSimpleName().toString());
                }
                fieldTypeName = typeArgs.get(0).toString();
            } else if (isMap) {
                List<TypeMirror> typeArgs = getTypeArguments(fieldElement);
                if (typeArgs.size() != 2) {
                    throw new KernelException(
                            "The map field [{}.{}] must be define only TWO type arguments",
                            classElemt.getSimpleName().toString(),
                            fieldElement.getSimpleName().toString());
                }
                idType = typeArgs.get(0).toString();
                Types typeUtils = builderCtx.getTypeUtils();
                Elements elemtUtils = builderCtx.getElementUtils();
                TypeElement identifiableElemt = elemtUtils.getTypeElement( IIdentifiable.class.getCanonicalName());
                DeclaredType identifiableType = typeUtils.getDeclaredType(identifiableElemt);
                if (! typeUtils.isAssignable(typeArgs.get(1), identifiableType)) {
                    throw new KernelException(
                            "The value type of the field [{}.{}] must be implement IIdentifiable interface",
                            classElemt.getSimpleName().toString(),
                            fieldElement.getSimpleName().toString());
                }
                fieldTypeName = typeArgs.get(1).toString();
            }

            Inject inject = fieldElement.getAnnotation(Inject.class);
            String injectId = inject.value();
            if (Strings.isNullOrEmpty(injectId)) {
                injectId = fieldTypeName;
            }
            String injectFrom = inject.from();
            if (Strings.isNullOrEmpty(injectFrom)) {
                throw new KernelException(
                        "The inject service from [{}.{}] must be specified",
                        classElemt.getSimpleName().toString(),
                        fieldElement.getSimpleName().toString());
            }

//            String paramName = SETTER_PARAM_NAME;
//            String code;
//            if (isCollection) {
//                code = StringHelper.makeString("{}.add({});", fieldName, paramName);
//            } else if (isMap) {
//                code = StringHelper.makeString("{}.put( ({}) (({}) {}).getId(), {} );",
//                        fieldName, idType, IIdentifiable.class.getCanonicalName(), paramName, paramName);
//            } else {
//                code = StringHelper.makeString("{}={};", fieldName, paramName);
//            }

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
//            clsBuilder
//                    .addImplement(IInjectable.class.getCanonicalName())
//                    .addMethodBuilder(SetterMeta.builder()
//                            .setIsSingle(! isCollection && ! isMap)
//                            .setFieldName(fieldName)
//                            .setInjectId(injectId)
//                            .setInjectFrom(injectFrom)
//                            .setInjectType(fieldTypeName)
//                            .setName(setterName)
//                            .setReturnTypeName(Type.VOID)
//                            .setInvokeSuper(MethodMeta.InvokeSuper.NONE)
//                            .addParameterBuilder(ParameterMeta.builder()
//                                    .addModifier(Modifier.FINAL)
//                                    .setName(paramName)
//                                    .setType(fieldTypeName))
//                            .addCodeBuilder(CodeMeta.builder()
//                                    .addRawCode(code)));

            addSetter(clsBuilder, fieldName, fieldTypeName, injectId, injectFrom, setterName, isCollection, isMap, idType, false);
//            builderCtx.getLogger().info("Handle service {}", clsBuilder.getClassName());
//            Looper.from(clsBuilder.findSetterBuilders()).foreach(builder -> builderCtx.getLogger().info("-->> {}", builder.getName()));
        });

        implementIInjectable(builderCtx);
        implementGetDependencies(builderCtx);
    }

    private void addSetter(
            final ClassMeta.Builder classBuilder,
            final String fieldName,
            final String fieldType,
            final String injectId,
            final String injectFrom,
            final String setterName,
            final boolean isCollection,
            final boolean isMap,
            final String mapKeyType,
            final boolean needGenerateField) {
        SetterMeta.Builder setterBdr = Looper.from(classBuilder.findSetterBuilders())
                .filter(methodBuilder -> methodBuilder.getName().equals(setterName))
                .map(methodBuilder -> (SetterMeta.Builder) methodBuilder)
                .filter(setterBuilder -> setterBuilder.getInjectType().equals(fieldType))
                .first(null);
        if (setterBdr != null) {
            return;
        }

        if (needGenerateField) {
            // Generate field
            FieldMeta.Builder fieldMeta = classBuilder.findFieldBuilder(fieldName, fieldType);
            if (fieldMeta == null) {
                classBuilder.addFieldBuilder(FieldMeta.builder()
                        .addModifier(Modifier.PRIVATE)
                        .setName(fieldName)
                        .setTypeName(fieldType)
                        .setIsList(isCollection)
                        .setIsMap(isMap));
            }
        }

        String paramName = SETTER_PARAM_NAME;
        String code;
        if (isCollection) {
            code = StringHelper.makeString("{}.add({});", fieldName, paramName);
        } else if (isMap) {
            code = StringHelper.makeString("{}.put( ({}) (({}) {}).getId(), {} );",
                    fieldName, mapKeyType, IIdentifiable.class.getCanonicalName(), paramName, paramName);
        } else {
            code = StringHelper.makeString("{}={};", fieldName, paramName);
        }
        classBuilder
                .addImplement(IInjectable.class.getCanonicalName())
                .addMethodBuilder(SetterMeta.builder()
                        .setIsSingle(! isCollection && ! isMap)
                        .setFieldName(fieldName)
                        .setInjectId(injectId)
                        .setInjectFrom(injectFrom)
                        .setInjectType(fieldType)
                        .setName(setterName)
                        .setReturnTypeName(Type.VOID)
                        .setInvokeSuper(MethodMeta.InvokeSuper.NONE)
                        .addParameterBuilder(ParameterMeta.builder()
                                .addModifier(Modifier.FINAL)
                                .setName(paramName)
                                .setType(fieldType))
                        .addCodeBuilder(CodeMeta.builder()
                                .addRawCode(code)));
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

    private boolean isMap(
            final Element fieldElement,
            final IBuilderContext builderCtx) {
        Elements elemtUtils = builderCtx.getElementUtils();
        Types typeUtils = builderCtx.getTypeUtils();
        WildcardType wildcardType = typeUtils.getWildcardType(null, null);
        TypeElement collectionTypeElemt = elemtUtils.getTypeElement(
                Map.class.getCanonicalName());
        DeclaredType mapType = typeUtils.getDeclaredType(
                collectionTypeElemt, wildcardType, wildcardType);
        return typeUtils.isAssignable(fieldElement.asType(), mapType);
    }

    private List<TypeMirror> getTypeArguments(Element fieldElement) {
        final List<TypeMirror> typeArgs = new ArrayList<>();
        DeclaredType declaredType = (DeclaredType) fieldElement.asType();
        declaredType.getTypeArguments().forEach(
                typeMirror -> typeArgs.add(typeMirror));
        return typeArgs;
    }

    private void implementGetDependencies(
            final IBuilderContext builderCtx
    ) throws KernelException {
        Template temp = builderCtx.loadTemplate(TEMPLATE_GET_DEPENDENCIES);
        builderCtx.getBuilders().forEach(classBuilder -> {
            implementGetDependenciesForClass(classBuilder, temp);
//            // Receive service dependency id list
//            List<MethodMeta.Builder> setterBuilders = classBuilder.findSetterBuilders();
//            List<DependencyModel> dependencies = Looper.from(setterBuilders)
//                    .map(builder -> (SetterMeta.Builder) builder)
//                    .map(setterBuilder -> {
//                        DependencyModel depModel = new DependencyModel(
//                                QualifiedServiceId.combine(setterBuilder.getInjectId(), setterBuilder.getInjectFrom()),
//                                setterBuilder.getInjectType());
//                        depModel.setSingle(setterBuilder.getIsSingle());
//                        return depModel;
//                    })
//                    .toList();
//            // Check duplicated dependency
//            dependencies.stream()
//                    .collect(Collectors.groupingBy(p -> p, Collectors.summingInt(p -> 1)))
//                    .forEach((dependSvc, counter) -> {
//                        if (counter > 1) {
//                            throw new KernelException(StringHelper.makeString(
//                                    "The service {}.{} has duplicated dependency on same service {}",
//                                    classBuilder.getPackageName(),
//                                    classBuilder.getClassName(),
//                                    dependSvc));
//                        }
//                    });
//            Template tempDependencies = builderCtx.loadTemplate(TEMPLATE_GET_DEPENDENCIES);
//            Map<String, Object> tempModelDependencies = new HashMap<>();
//            tempModelDependencies.put("dependencies", dependencies);
//            if (classBuilder.findSetterBuilders().size() == 0) {
//                // No setters means this class does not implement IInjectable interface
//                return;
//            }
//            classBuilder.addMethodBuilder(MethodMeta.builder()
//                    .addAnnotationBuilder(AnnotationMeta.builder()
//                            .setName(AnnotationMeta.OVERRIDE))
//                    .setName("getDependencies")
//                    .addModifier(Modifier.PUBLIC)
//                    .setReturnTypeName(StringHelper.makeString("{}[]", Dependency.class.getName()))
//                    .addCodeBuilder(CodeMeta.builder()
//                            .setTemplate(tempDependencies)
//                            .setModel(tempModelDependencies)));
        });
    }

    private void implementGetDependenciesForClass(ClassMeta.Builder classBuilder, Template temp) {
        // Receive service dependency id list
        List<MethodMeta.Builder> setterBuilders = classBuilder.findSetterBuilders();
        List<DependencyModel> dependencies = Looper.from(setterBuilders)
                .map(builder -> (SetterMeta.Builder) builder)
                .map(setterBuilder -> {
                    DependencyModel depModel = new DependencyModel(
                            QualifiedServiceId.combine(setterBuilder.getInjectId(), setterBuilder.getInjectFrom()),
                            setterBuilder.getInjectType());
                    depModel.setSingle(setterBuilder.getIsSingle());
                    return depModel;
                })
                .toList();
        // Check duplicated dependency
        dependencies.stream()
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
//        Template tempDependencies = builderCtx.loadTemplate(TEMPLATE_GET_DEPENDENCIES);
        Map<String, Object> tempModelDependencies = new HashMap<>();
        tempModelDependencies.put("dependencies", dependencies);
        if (classBuilder.findSetterBuilders().size() == 0) {
            // No setters means this class does not implement IInjectable interface
            return;
        }
        classBuilder.overrideMethodBuilder(MethodMeta.builder()
                .addAnnotationBuilder(AnnotationMeta.builder()
                        .setName(AnnotationMeta.OVERRIDE))
                .setName("getDependencies")
                .addModifier(Modifier.PUBLIC)
                .setReturnTypeName(StringHelper.makeString("{}[]", Dependency.class.getName()))
                .addCodeBuilder(CodeMeta.builder()
                        .setTemplate(temp)
                        .setModel(tempModelDependencies)));
    }

    private void implementIInjectable(
            final IBuilderContext builderContext
    ) throws KernelException {
        Template temp = builderContext.loadTemplate(TEMPLATE_FILE);

//        String methodName = "injectObject";
//        String paramName = "injection";
//        String paramType = Injection.class.getName();
        builderContext.getBuilders().forEach(classBuilder -> {
            implementInjectObjectForClass(classBuilder, temp);
//            final List<SetterModel> setterModels = new ArrayList<>();
//            classBuilder.findSetterBuilders().forEach(methodBuilder -> {
//                SetterMeta.Builder setterBuilder = (SetterMeta.Builder) methodBuilder;
//                setterModels.add(new SetterModel(
//                        setterBuilder.getName(),
//                        setterBuilder.getInjectId(),
//                        setterBuilder.getInjectType()));
//            });
//            Map<String, Object> tempModel = new HashMap<>();
//            tempModel.put("setters", setterModels);
//
//            if (classBuilder.findSetterBuilders().size() == 0) {
//                // No setters means this class does not implement IInjectable interface
//                return;
//            }
//            classBuilder
//                    .addImplement(IInjectable.class.getCanonicalName())
//                    .addMethodBuilder(MethodMeta.builder()
//                            .addAnnotationBuilder(AnnotationMeta.builder()
//                                    .setName("Override"))
//                            .addModifier(Modifier.PUBLIC)
//                            .setName(methodName)
//                            .setReturnTypeName(Type.VOID)
//                            .addThrowTypeName(InvalidArgumentException.class.getCanonicalName())
//                            .addParameterBuilder(ParameterMeta.builder()
//                                    .addModifier(Modifier.FINAL)
//                                    .setName(paramName)
//                                    .setType(paramType))
//                            .addCodeBuilder(CodeMeta.builder()
//                                    .setModel(tempModel)
//                                    .setTemplate(temp)));
        });
    }

    private void implementInjectObjectForClass(ClassMeta.Builder classBuilder, Template temp) {
        String methodName = "injectObject";
        String paramName = "injection";
        String paramType = Injection.class.getName();

        final List<SetterModel> setterModels = new ArrayList<>();
        classBuilder.findSetterBuilders().forEach(methodBuilder -> {
            SetterMeta.Builder setterBuilder = (SetterMeta.Builder) methodBuilder;
            setterModels.add(new SetterModel(
                    setterBuilder.getName(),
                    setterBuilder.getInjectId(),
                    setterBuilder.getInjectType()));
        });
        Map<String, Object> tempModel = new HashMap<>();
        tempModel.put("setters", setterModels);

        if (classBuilder.findSetterBuilders().size() == 0) {
            // No setters means this class does not implement IInjectable interface
            return;
        }
        classBuilder
                .addImplement(IInjectable.class.getCanonicalName())
                .overrideMethodBuilder(MethodMeta.builder()
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
//        classBuilder
//                .addImplement(IInjectable.class.getCanonicalName())
//                .addMethodBuilder(MethodMeta.builder()
//                        .addAnnotationBuilder(AnnotationMeta.builder()
//                                .setName("Override"))
//                        .addModifier(Modifier.PUBLIC)
//                        .setName(methodName)
//                        .setReturnTypeName(Type.VOID)
//                        .addThrowTypeName(InvalidArgumentException.class.getCanonicalName())
//                        .addParameterBuilder(ParameterMeta.builder()
//                                .addModifier(Modifier.FINAL)
//                                .setName(paramName)
//                                .setType(paramType))
//                        .addCodeBuilder(CodeMeta.builder()
//                                .setModel(tempModel)
//                                .setTemplate(temp)));
    }

    class InjectParserHelper {

        public void addDependency(
                final IBuilderContext builderContext,
                final ClassMeta.Builder classBuilder,
                final String fieldName,
                final String fieldType,
                final String injectId,
                final String injectFrom,
                final boolean isCollection,
                final boolean isMap,
                final String mapKeyType) {
            String setterName = ClassHelper.makeSetterName(fieldName, isCollection, isMap);
            InjectParser.this.addSetter(classBuilder, fieldName, fieldType, injectId, injectFrom, setterName, isCollection, isMap, mapKeyType, true);
            Template tempInjectObject = builderContext.loadTemplate(TEMPLATE_FILE);
            Template tempGetDependencies = builderContext.loadTemplate(TEMPLATE_GET_DEPENDENCIES);
            implementInjectObjectForClass(classBuilder, tempInjectObject);
            implementGetDependenciesForClass(classBuilder, tempGetDependencies);
        }
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

    public static final class DependencyModel {

        private String _qSvcId;
        private String _svcType;
        private boolean _optional;
        private boolean _single;

        private DependencyModel(
                final String qualifiedServiceId,
                final String serviceType) {
            this._qSvcId = qualifiedServiceId;
            this._svcType = serviceType;
        }

        public String getQualifiedServiceId() {
            return this._qSvcId;
        }

        public String getServiceType() {
            return this._svcType;
        }

        public void setOptional(boolean optional) {
            this._optional = optional;
        }

        public boolean getOptional() {
            return this._optional;
        }

        public void setSingle(boolean single) {
            this._single = single;
        }

        public boolean getSingle() {
            return this._single;
        }
    }
}
