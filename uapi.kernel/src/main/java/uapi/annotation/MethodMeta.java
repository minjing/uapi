package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A meta class for method
 */
public class MethodMeta {

    private final Builder _builder;

    private MethodMeta(
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

    public String getReturnTypeName() {
        return this._builder._rtnTypeName;
    }

    public List<ParameterMeta> getParameters() {
        return this._builder._params;
    }

    public List<String> getThrowTypeNames() {
        return this._builder._throwTypeNames;
    }

    public List<String> getCodes() {
        return this._builder._codes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(
            final Element methodElement,
            final BuilderContext builderContext
    ) throws KernelException {
        ArgumentChecker.notNull(methodElement, "methodElement");
        ArgumentChecker.notNull(builderContext, "builderContext");
        if (methodElement.getKind() != ElementKind.METHOD) {
            throw new KernelException(
                    "The element is not a method element - {}",
                    methodElement.getSimpleName().toString());
        }

        Builder builder = new Builder();
        ExecutableElement execElem = (ExecutableElement) methodElement.asType();
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

    public static final class Builder extends uapi.helper.Builder<MethodMeta> {

        private String _name;
        private List<Modifier> _modifiers = new ArrayList<>();
        private String _rtnTypeName;
        private List<ParameterMeta> _params = new ArrayList<>();
        private List<ParameterMeta.Builder> _paramBuilders = new ArrayList<>();
        private List<String> _throwTypeNames = new ArrayList<>();
        private List<String> _codes = new ArrayList<>();

        private Builder() { }

        public Builder setName(
                final String name
        ) throws KernelException {
            checkStatus();
            this._name = name;
            return this;
        }

        public void addModifier(Modifier modifier) {
            checkStatus();
            ArgumentChecker.notNull(modifier, "modifier");
            this._modifiers.add(modifier);
        }

        public Builder setReturnTypeName(
                final String typeName
        ) throws KernelException {
            checkStatus();
            this._rtnTypeName = typeName;
            return this;
        }

        public Builder addParameterBuilder(
                final ParameterMeta.Builder parameterBuilder
        ) throws KernelException {
            checkStatus();
            this._paramBuilders.add(parameterBuilder);
            return this;
        }

        public ParameterMeta.Builder getParameterBuilder(
                final String parameterName
        ) {
            for (ParameterMeta.Builder paramBuilder : this._paramBuilders) {
                if (parameterName.equals(paramBuilder.getName())) {
                    return paramBuilder;
                }
            }
            return null;
        }

        public Builder addThrowTypeName(
                String typeName
        ) throws KernelException {
            checkStatus();
            this._throwTypeNames.add(typeName);
            return this;
        }

        public Builder addCodes(
                final String code
        ) throws KernelException {
            checkStatus();
            this._codes.add(code);
            return this;
        }

        public ParameterMeta.Builder findParameterBuilder(
                final Element parameterElement,
                final BuilderContext builderContext
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

        @Override
        protected MethodMeta buildInstance(
        ) throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._name, "name");
            ArgumentChecker.notEmpty(this._rtnTypeName, "returnTypeName");
            this._paramBuilders.forEach(parameterBuilder ->
                this._params.add(parameterBuilder.buildInstance()));
            return new MethodMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "MethodMeta[" +
                            "name={}, " +
                            "modifiers={}, " +
                            "returnTypeName={}, " +
                            "parameters={}, " +
                            "throwTypeNames={}, " +
                            "codes={}]",
                    this._name,
                    this._modifiers,
                    this._rtnTypeName,
                    this._paramBuilders,
                    this._throwTypeNames,
                    this._codes
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(this._name, builder._name) &&
                    Objects.equals(this._modifiers, builder._modifiers) &&
                    Objects.equals(this._rtnTypeName, builder._rtnTypeName) &&
                    Objects.equals(this._paramBuilders, builder._paramBuilders) &&
                    Objects.equals(this._throwTypeNames, builder._throwTypeNames);
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
}
