package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public final class ClassMeta {

    private final Builder _builder;

    private ClassMeta(
            final Builder builder
    ) {
        this._builder = builder;
    }

    public String getPackageName() {
        return this._builder._pkgName;
    }

    public String getClassName() {
        return this._builder._className;
    }

    public String getGeneratedClassName() {
        return this._builder._generatedClassName;
    }

    public List<AnnotationMeta> getAnnotations() {
        return this._builder._annotations;
    }

    public List<FieldMeta> getProperties() {
        return this._builder._properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return this._builder.toString();
    }

    public static final class Builder extends uapi.helper.Builder<ClassMeta> {

        private String _pkgName;
        private String _className;
        private String _generatedClassName;
        private List<String> _implements = new ArrayList<>();
        private List<AnnotationMeta> _annotations = new ArrayList<>();
        private List<FieldMeta> _properties = new ArrayList<>();

        private Builder() { }

        public Builder setPackageName(
                final String packageName
        ) throws KernelException {
            checkStatus();
            this._pkgName = packageName;
            return this;
        }

        public Builder setClassName(
                final String serviceClassName
        ) throws KernelException {
            checkStatus();
            this._className = serviceClassName;
            return this;
        }

        public Builder addAnnotation(
                final AnnotationMeta annotationMeta
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(annotationMeta, "annotationMeta");
            this._annotations.add(annotationMeta);
            return this;
        }

        public Builder addImplement(
                final String implement
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(implement, "implement");
            return this;
        }

        public Builder addProperty(
                final FieldMeta fieldMeta
        ) throws KernelException {
            checkStatus();
            this._properties.add(fieldMeta);
            return this;
        }

        public Builder setGeneratedClassName(
                final String generatedClassName
        ) throws KernelException {
            checkStatus();
            this._generatedClassName = generatedClassName;
            return this;
        }

        public String getGeneratedClassName() {
            return this._generatedClassName;
        }

        @Override
        protected ClassMeta buildInstance(
        ) throws InvalidArgumentException {
            ArgumentChecker.required(this._pkgName, "packageName");
            ArgumentChecker.required(this._className, "className");
            ArgumentChecker.required(this._generatedClassName, "generatedClassName");
            return new ClassMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "ClassMeta[" +
                            "packageName={}, " +
                            "className={}, " +
                            "generatedClassName={}, " +
                            "annotations={}, " +
                            "properties={}]",
                    this._pkgName,
                    this._className,
                    this._annotations,
                    this._properties,
                    this._generatedClassName);
        }
    }
}
