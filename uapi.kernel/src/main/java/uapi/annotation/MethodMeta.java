package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

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

    public Scope getScope() {
        return this._builder._scope;
    }

    public boolean getIsStatic() {
        return this._builder._isStatic;
    }

    public String getModifier() {
        return StringHelper.makeString(
                "{} {}",
                this._builder._scope.getValue(),
                this._builder._isStatic ? "static" : StringHelper.EMPTY
        );
    }

    public String getReturnTypeName() {
        return this._builder._rtnTypeName;
    }

    public List<ParameterMeta> getParameters() {
        return this._builder._parameters;
    }

    public List<String> getThrowTypeNames() {
        return this._builder._throwTypeNames;
    }

    public List<String> getCodes() {
        return this._builder._codes;
    }

    public enum Scope {
        PUBLIC("public"),
        PRIVATE("private"),
        PROTECTED("protected");

        private final String _value;

        Scope(String value) {
            this._value = value;
        }

        String getValue() {
            return this._value;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends uapi.helper.Builder<MethodMeta> {

        private String _name;
        private Scope _scope;
        private boolean _isStatic;
        private String _rtnTypeName;
        private List<ParameterMeta> _parameters = new ArrayList<>();
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

        public Builder setScope(
                final Scope scope
        ) throws KernelException {
            checkStatus();
            this._scope = scope;
            return this;
        }

        public Builder setIsStatic(
                final boolean isStatic
        ) throws KernelException {
            checkStatus();
            this._isStatic = isStatic;
            return this;
        }

        public Builder setReturnTypeName(
                final String typeName
        ) throws KernelException {
            checkStatus();
            this._rtnTypeName = typeName;
            return this;
        }

        public Builder addParameter(
                final ParameterMeta parameter
        ) throws KernelException {
            checkStatus();
            this._parameters.add(parameter);
            return this;
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

        @Override
        protected MethodMeta buildInstance(
        ) throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._name, "name");
            ArgumentChecker.notNull(this._scope, "scope");
            ArgumentChecker.notEmpty(this._rtnTypeName, "returnTypeName");
            return new MethodMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "MethodMeta[" +
                            "name={}, " +
                            "scope={}, " +
                            "isStatic={}, " +
                            "returnTypeName={}, " +
                            "parameters={}, " +
                            "throwTypeNames={}, " +
                            "codes={}]",
                    this._name,
                    this._scope,
                    this._isStatic,
                    this._rtnTypeName,
                    this._parameters,
                    this._throwTypeNames,
                    this._codes
            );
        }
    }
}
