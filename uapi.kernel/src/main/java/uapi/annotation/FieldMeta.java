package uapi.annotation;

import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

public final class FieldMeta {

    private Builder _builder;

    private FieldMeta(final Builder builder) {
        this._builder = builder;
    }

    public String getName() {
        return this._builder._name;
    }

    public String getTypeName() {
        return this._builder._typeName;
    }

    public String getInjectServiceId() {
        return this._builder._injectServiceId;
    }

    public boolean getIsList() {
        return this._builder._isList;
    }

    @Override
    public String toString() {
        return this._builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder for <code>FieldMeta</code>
     */
    static final class Builder extends uapi.helper.Builder<FieldMeta> {

        private String _name;
        private String _typeName;
        private String _injectServiceId;
        private boolean _isList;

        public Builder setName(
                final String name
        ) {
            checkStatus();
            this._name = name;
            return this;
        }

        public Builder setTypeName(
                final String typeName
        ) {
            checkStatus();
            this._typeName = typeName;
            return this;
        }

        public Builder setInjectServiceId(
                final String injectServiceId
        ) {
            checkStatus();
            this._injectServiceId = injectServiceId;
            return this;
        }

        public Builder setIsList(
                final boolean isList
        ) {
            checkStatus();
            this._isList = isList;
            return this;
        }

        @Override
        protected FieldMeta buildInstance() {
            checkStatus();
            ArgumentChecker.required(this._name, "fieldName");
            ArgumentChecker.required(this._typeName, "fieldTypeName");
            ArgumentChecker.required(this._injectServiceId, "injectServiceId");
            return new FieldMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "fieldName={}\nfieldTypeName={}\ninjectServiceId={}",
                    this._name, this._typeName, this._injectServiceId);
        }
    }
}
