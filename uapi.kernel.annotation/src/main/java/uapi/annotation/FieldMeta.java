package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

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

    public String getModifiers() {
        return CollectionHelper.asString(this._builder._modifiers, " ");
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
        private List<Modifier> _modifiers = new ArrayList<>();

        public Builder setName(
                final String name
        ) throws KernelException {
            checkStatus();
            this._name = name;
            return this;
        }

        public Builder setTypeName(
                final String typeName
        ) throws KernelException {
            checkStatus();
            this._typeName = typeName;
            return this;
        }

        public Builder setInjectServiceId(
                final String injectServiceId
        ) throws KernelException {
            checkStatus();
            this._injectServiceId = injectServiceId;
            return this;
        }

        public Builder setIsList(
                final boolean isList
        ) throws KernelException {
            checkStatus();
            this._isList = isList;
            return this;
        }

        public Builder addModifier(
                final Modifier modifier
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(modifier, "modifier");
            return this;
        }


        @Override
        protected void validation() throws InvalidArgumentException {
            ArgumentChecker.required(this._name, "fieldName");
            ArgumentChecker.required(this._typeName, "fieldTypeName");
            //ArgumentChecker.required(this._injectServiceId, "injectServiceId");
        }

        @Override
        protected void initProperties() { }

        @Override
        protected FieldMeta createInstance() {
            return new FieldMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "FieldMeta[" +
                            "fieldName={}, " +
                            "fieldTypeName={}," +
                            "injectServiceId={}",
                    this._name,
                    this._typeName,
                    this._injectServiceId);
        }
    }
}
