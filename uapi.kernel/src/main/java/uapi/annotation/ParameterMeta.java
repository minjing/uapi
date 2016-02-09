package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

/**
 * A meta object for a formal parameter
 */
public class ParameterMeta {

    private final Builder _builder;

    public ParameterMeta(
            final Builder builder
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(builder, "builder");
        this._builder = builder;
    }

    public String getName() {
        return this._builder._name;
    }

    public String getType() {
        return this._builder._type;
    }

    public boolean getNotNull() {
        return this._builder._notNull;
    }

    public static final class Builder extends uapi.helper.Builder<ParameterMeta> {

        private String _name;
        private String _type;
        private boolean _notNull;

        public Builder setName(
                final String name
        ) throws KernelException {
            checkStatus();
            this._name = name;
            return this;
        }

        public Builder setType(
                final String type
        ) throws KernelException {
            checkStatus();
            this._type = type;
            return this;
        }

        public Builder setNotNull(
                final boolean notNull
        ) throws KernelException {
            checkStatus();
            this._notNull = notNull;
            return this;
        }

        @Override
        protected ParameterMeta buildInstance() {
            ArgumentChecker.notEmpty(this._name, "name");
            ArgumentChecker.notEmpty(this._type, "type");
            return new ParameterMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "ParameterMeta[" +
                            "name={}, " +
                            "type={}, " +
                            "notNull={}]",
                    this._name, this._type, this._notNull
            );
        }
    }
}
