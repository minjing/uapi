/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A meta class for method
 */
public class MethodMeta {

    protected final Builder _builder;

    protected MethodMeta(
            final Builder builder
    ) {
        this._builder = builder;
    }

    public String getName() {
        return this._builder._name;
    }

    public String getModifiers() {
        return CollectionHelper.asString(this._builder._modifiers, " ");
    }

    public List<AnnotationMeta> getAnnotations() {
        return this._builder._annos;
    }

    public String getReturnTypeName() {
        return this._builder._rtnTypeName;
    }

    public boolean getIsSetter() {
        return this._builder._isSetter;
    }

    public boolean getInvokeSuperBefore() {
        return this._builder._invokeSuper == InvokeSuper.BEFORE;
    }

    public boolean getInvokeSuperAfter() {
        return this._builder._invokeSuper == InvokeSuper.AFTER;
    }

    public List<ParameterMeta> getParameters() {
        return this._builder._params;
    }

    public Set<String> getThrowTypeNames() {
        return this._builder._throwTypeNames;
    }

    public List<CodeMeta> getCodes() {
        return this._builder._codes;
    }

    @Override
    public String toString() {
        return this._builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(
            final Element methodElement,
            final IBuilderContext builderContext
    ) throws KernelException {
        ArgumentChecker.notNull(methodElement, "methodElement");
        ArgumentChecker.notNull(builderContext, "builderContext");
        if (methodElement.getKind() != ElementKind.METHOD) {
            throw new KernelException(
                    "The element is not a method element - {}",
                    methodElement.getSimpleName().toString());
        }

        Builder builder = new Builder();
        ExecutableElement execElem = (ExecutableElement) methodElement;
        builder.setName(methodElement.getSimpleName().toString())
                .setReturnTypeName(execElem.getReturnType().toString());
        execElem.getModifiers().forEach(modifier ->
            builder.addModifier(modifier));
        execElem.getThrownTypes().forEach(throwType ->
                builder.addThrowTypeName(throwType.toString()));
        execElem.getParameters().forEach(paramElem ->
            builder.addParameterBuilder(
                    ParameterMeta.builder(paramElem, builderContext)));
        return builder;
    }

    public static class Builder extends uapi.helper.Builder<MethodMeta> {

        private String _name;
        private List<Modifier> _modifiers = new ArrayList<>();
        private List<AnnotationMeta> _annos = new ArrayList<>();
        private List<AnnotationMeta.Builder> _annoBuilders = new ArrayList<>();
        private String _rtnTypeName;
        private boolean _isSetter = false;
        private InvokeSuper _invokeSuper = InvokeSuper.NONE;
        private List<ParameterMeta> _params = new ArrayList<>();
        private List<ParameterMeta.Builder> _paramBuilders = new ArrayList<>();
        private Set<String> _throwTypeNames = new HashSet<>();
        private List<CodeMeta> _codes = new ArrayList<>();
        private List<CodeMeta.Builder> _codeBuilders = new ArrayList<>();

        protected Builder() { }

        public Builder setName(
                final String name
        ) throws KernelException {
            checkStatus();
            this._name = name;
            return this;
        }

        public String getName() {
            return this._name;
        }

        public Builder addModifier(Modifier modifier) {
            checkStatus();
            ArgumentChecker.notNull(modifier, "modifier");
            this._modifiers.add(modifier);
            return this;
        }

        public Builder addAnnotationBuilder(
                final AnnotationMeta.Builder builder
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(builder, "builder");
            this._annoBuilders.add(builder);
            return this;
        }

        public Builder setReturnTypeName(
                final String typeName
        ) throws KernelException {
            checkStatus();
            this._rtnTypeName = typeName;
            return this;
        }

        public String getReturnTypeName() {
            return this._rtnTypeName;
        }

        public Builder setInvokeSuper(
                final InvokeSuper invokeSuper
        ) throws KernelException {
            checkStatus();
            if (this._invokeSuper != InvokeSuper.NONE) {
                throw new KernelException(
                        "The invoke super can be set only once, current: {}, request: {}",
                        this._invokeSuper, invokeSuper);
            }
            this._invokeSuper = invokeSuper;
            return this;
        }

        public Builder setIsSetter(
                final boolean isSetter
        ) throws KernelException {
            checkStatus();
            this._isSetter = isSetter;
            return this;
        }

        public boolean isSetter() {
            return this._isSetter;
        }

        public Builder addParameterBuilder(
                final ParameterMeta.Builder parameterBuilder
        ) throws KernelException {
            checkStatus();
            this._paramBuilders.add(parameterBuilder);
            return this;
        }

//        public ParameterMeta.Builder getParameterBuilder(
//                final String parameterName
//        ) {
//            for (ParameterMeta.Builder paramBuilder : this._paramBuilders) {
//                if (parameterName.equals(paramBuilder.getName())) {
//                    return paramBuilder;
//                }
//            }
//            return null;
//        }

        public int getParameterCount() {
            return this._paramBuilders.size();
        }

        public Builder addThrowTypeName(
                String typeName
        ) throws KernelException {
            checkStatus();
            this._throwTypeNames.add(typeName);
            return this;
        }

        public Builder addCodeBuilder(
                final CodeMeta.Builder codeBuilder
        ) throws InvalidArgumentException {
            checkStatus();
            ArgumentChecker.notNull(codeBuilder, "codeBuilder");
            this._codeBuilders.add(codeBuilder);
            return this;
        }

//        public CodeMeta.Builder addCodeBuilderIfAbsent(
//                final CodeMeta.Builder codeBuilder
//        ) throws InvalidArgumentException {
//            checkStatus();
//            ArgumentChecker.notNull(codeBuilder, "codeBuilder");
//            List<CodeMeta.Builder> matchedBuilders = this._codeBuilders.parallelStream()
//                    .filter(existing -> existing.equals(codeBuilder))
//                    .collect(Collectors.toList());
//            if (matchedBuilders.size() == 0) {
//                addCodeBuilder(codeBuilder);
//                return codeBuilder;
//            }
//            if (matchedBuilders.size() == 1) {
//                return matchedBuilders.get(0);
//            }
//            throw new KernelException("Found not only one code builder - {}" + matchedBuilders);
//        }

        public CodeMeta.Builder findCodeBuilder(
                final String templateSourceName
        ) throws InvalidArgumentException {
            ArgumentChecker.notNull(templateSourceName, "templateSourceName");
            List<CodeMeta.Builder> matchedBuilders = this._codeBuilders.parallelStream()
                    .filter(existing -> templateSourceName.equals(existing.getTemplateSourceName()))
                    .collect(Collectors.toList());
            if (matchedBuilders.size() == 0) {
                return null;
            }
            if (matchedBuilders.size() == 1) {
                return matchedBuilders.get(0);
            }
            throw new KernelException("Found more than one code builder associated with tempate source name - {}", templateSourceName);
        }

        public ParameterMeta.Builder findParameterBuilder(
                final Element parameterElement,
                final IBuilderContext builderContext
        ) throws KernelException {
            ArgumentChecker.notNull(parameterElement, "parameterElement");
            ArgumentChecker.notNull(builderContext, "builderContext");
            ParameterMeta.Builder paramBuilder =
                    ParameterMeta.builder(parameterElement, builderContext);
            List<ParameterMeta.Builder> matchedBuilders = this._paramBuilders.parallelStream()
                    .filter(existing -> existing.equals(paramBuilder))
                    .collect(Collectors.toList());
            if (matchedBuilders.size() != 1) {
                throw new KernelException(
                        "Expect only one matched parameter builder for {}, but found {} ",
                        paramBuilder, matchedBuilders.size());
            }
            return matchedBuilders.get(0);
        }

        public ParameterMeta.Builder findParameterBuilder(
                final String name
        ) throws KernelException {
            ArgumentChecker.notEmpty(name, "name");
            List<ParameterMeta.Builder> params = this._paramBuilders.stream()
                    .filter(paramBuilder -> name.equals(paramBuilder.getName()))
                    .collect(Collectors.toList());
            if (params.size() == 0) {
                throw new KernelException("Can't found parameter named {} at method {}", name, this._name);
            } else if (params.size() > 1) {
                throw new KernelException("Find duplicate parameter named {} at method {}", name, this._name);
            }
            return params.get(0);
        }

        @Override
        protected void validation() throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._name, "name");
            ArgumentChecker.notEmpty(this._rtnTypeName, "returnTypeName");
            this._annoBuilders.forEach(AnnotationMeta.Builder::validation);
            this._paramBuilders.forEach(ParameterMeta.Builder::validation);
            this._codeBuilders.forEach(CodeMeta.Builder::validation);
        }

        @Override
        protected void initProperties() {
            this._annoBuilders.forEach(annoBuilder -> {
                annoBuilder.initProperties();
                this._annos.add(annoBuilder.createInstance());
            });
            this._paramBuilders.forEach(paramBuilder -> {
                paramBuilder.initProperties();
                this._params.add(paramBuilder.createInstance());
            });
            this._codeBuilders.forEach(codeBuilder -> {
                codeBuilder.initProperties();
                this._codes.add(codeBuilder.createInstance());
            });
        }

        @Override
        protected MethodMeta createInstance() {
            return new MethodMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "MethodMeta[" +
                            "annotation={}, " +
                            "annotationBuilders={}, " +
                            "name={}, " +
                            "modifiers={}, " +
                            "returnTypeName={}, " +
                            "isSetter={}, " +
                            "parameters={}, " +
                            "throwTypeNames={}, " +
                            "invokeSuper={}, " +
                            "codes={}, " +
                            "codeBuilders={}]",
                    this._annos,
                    this._annoBuilders,
                    this._name,
                    this._modifiers,
                    this._rtnTypeName,
                    this._isSetter,
                    this._paramBuilders,
                    this._throwTypeNames,
                    this._invokeSuper,
                    this._codes,
                    this._codeBuilders
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(this._name, builder._name) &&
                    CollectionHelper.equals(this._modifiers, builder._modifiers) &&
                    Objects.equals(this._rtnTypeName, builder._rtnTypeName) &&
                    CollectionHelper.equals(this._paramBuilders, builder._paramBuilders) &&
                    CollectionHelper.equals(this._throwTypeNames, builder._throwTypeNames);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this._name,
                    this._modifiers,
                    this._rtnTypeName,
                    this._paramBuilders,
                    this._throwTypeNames);
        }
    }

    public enum InvokeSuper {
        // Call super method before execute self statement
        BEFORE,

        // Call super method after execute self statement
        AFTER,

        // Do not call super method any more
        NONE
    }
}
