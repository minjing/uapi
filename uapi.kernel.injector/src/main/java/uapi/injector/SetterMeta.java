package uapi.injector;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.annotation.MethodMeta;
import uapi.helper.ArgumentChecker;

/**
 * Created by xquan on 2/19/2016.
 */
public class SetterMeta extends MethodMeta {

    private SetterMeta(Builder builder) {
        super(builder);
    }

    public String getInjectType() {
        return ((Builder) this._builder)._injectType;
    }

    public boolean getIsOptional() {
        return ((Builder) this._builder)._isOptional;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends MethodMeta.Builder {

        private String _fieldName;
        private String _injectId;
        private String _injectType;
        private boolean _isOptional = false;

        private Builder() {
            super.setIsSetter(true);
        }

        @Override
        public Builder setIsSetter(
                final boolean isSetter
        ) throws KernelException {
            checkStatus();
            if (! isSetter) {
                throw new KernelException(
                        "The setter method isSetter property can't be set to false");
            }
            return this;
        }

        public Builder setFieldName(
                final String fieldName
        ) throws InvalidArgumentException {
            checkStatus();
            ArgumentChecker.notEmpty(fieldName, "fieldName");
            this._fieldName = fieldName;
            return this;
        }

        public String getFieldName() {
            return this._fieldName;
        }

        public Builder setInjectId(
                final String injectId
        ) throws InvalidArgumentException {
            checkStatus();
            ArgumentChecker.notEmpty(injectId, "injectId");
            this._injectId = injectId;
            return this;
        }

        public String getInjectId() {
            return this._injectId;
        }

        public Builder setInjectType(
                final String type
        ) throws InvalidArgumentException {
            checkStatus();
            ArgumentChecker.notEmpty(type, "type");
            this._injectType = type;
            return this;
        }

        public String getInjectType() {
            return this._injectType;
        }

        public Builder setIsOptional(
                final boolean isOptional) {
            checkStatus();
            this._isOptional = isOptional;
            return this;
        }

        public boolean getIsOptional() {
            return this._isOptional;
        }

        @Override
        protected void validation() {
            super.validation();
            ArgumentChecker.notEmpty(this._fieldName, "fieldName");
            ArgumentChecker.notEmpty(this._injectType, "injectType");
        }

        @Override
        protected void initProperties() {
            super.initProperties();
        }

        @Override
        protected SetterMeta createInstance() {
            return new SetterMeta(this);
        }
    }
}
