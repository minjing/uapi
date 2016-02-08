package uapi.injector;

import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public final class ServiceMeta {

    private final Builder _builder;

    private ServiceMeta(
            final Builder builder
    ) {
        this._builder = builder;
    }

    public String getServicePackageName() {
        return this._builder._svcPackageName;
    }

    public String getServiceClassName() {
        return this._builder._svcClassName;
    }

    public String getGeneratedClassName() {
        return this._builder._generatedClassName;
    }

    public List<FieldMeta> getFieldMetas() {
        return this._builder._fieldMetas;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return this._builder.toString();
    }

    public static final class Builder extends uapi.helper.Builder<ServiceMeta> {

        private String _svcPackageName;
        private String _svcClassName;
        private String _generatedClassName;
        private List<FieldMeta> _fieldMetas = new ArrayList<>();

        private Builder() { }

        public Builder setServicePackageName(
                final String servicePackageName
        ) {
            checkStatus();
            this._svcPackageName = servicePackageName;
            return this;
        }

        public Builder setServiceClassName(
                final String serviceClassName
        ) {
            checkStatus();
            this._svcClassName = serviceClassName;
            return this;
        }

        public Builder addFieldMeta(
                final FieldMeta fieldMeta
        ) {
            checkStatus();
            this._fieldMetas.add(fieldMeta);
            return this;
        }

        public Builder setGeneratedClassName(
                final String generatedClassName
        ) {
            checkStatus();
            this._generatedClassName = generatedClassName;
            return this;
        }

        public String getGeneratedClassName() {
            return this._generatedClassName;
        }

        @Override
        protected ServiceMeta buildInstance() {
            ArgumentChecker.required(this._svcPackageName, "servicePackageName");
            ArgumentChecker.required(this._svcClassName, "serviceClassName");
            ArgumentChecker.required(this._generatedClassName, "generatedClassName");
            return new ServiceMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString("\tservicePackageName={}, serviceClassName={}, fieldMetas={}, generatedClassName={}",
                    this._svcPackageName, this._svcClassName, this._fieldMetas, this.getGeneratedClassName());
        }
    }
}
