package uapi.injector;

import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

public class FieldMeta {

    private Builder _builder;

    private FieldMeta(final Builder builder) {
        this._builder = builder;
    }

    public String getFieldName() {
        return this._builder._fieldName;
    }

    public String getFieldTypeName() {
        return this._builder._fieldTypeName;
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
    public static final class Builder extends uapi.helper.Builder<FieldMeta> {

        private String _fieldName;
        private String _fieldTypeName;
        private String _injectServiceId;
        private boolean _isList;

        public Builder setFieldName(
                final String fieldName
        ) {
            checkStatus();
            this._fieldName = fieldName;
            return this;
        }

        public Builder setFieldTypeName(
                final String fieldTypeName
        ) {
            checkStatus();
            this._fieldTypeName = fieldTypeName;
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
            ArgumentChecker.required(this._fieldName, "fieldName");
            ArgumentChecker.required(this._fieldTypeName, "fieldTypeName");
            ArgumentChecker.required(this._injectServiceId, "injectServiceId");
            return new FieldMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "fieldName={}\nfieldTypeName={}\ninjectServiceId={}",
                    this._fieldName, this._fieldTypeName, this._injectServiceId);
        }
    }
}
