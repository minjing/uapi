package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public List<String> getImports() {
        return this._builder._imports;
    }

    public List<String> getImplements() {
        return this._builder._implements;
    }

    public List<AnnotationMeta> getAnnotations() {
        return this._builder._annotations;
    }

    public List<FieldMeta> getProperties() {
        return this._builder._properties;
    }

    public List<MethodMeta> getMethods() {
        return this._builder._methods;
    }

    @Override
    public String toString() {
        return this._builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(
            final Element classElement,
            final BuilderContext builderContext
    ) throws KernelException {
        ArgumentChecker.notNull(classElement, "classElement");
        ArgumentChecker.notNull(builderContext, "builderContext");
        if (classElement.getKind() != ElementKind.CLASS) {
            throw new KernelException(
                    "The element is not a class element - {}",
                    classElement);
        }

        PackageElement pkgElemt = builderContext.getElementUtils().getPackageOf(classElement);
        return builder()
                .setPackageName(pkgElemt.getQualifiedName().toString())
                .setClassName(classElement.getSimpleName().toString())
                .setGeneratedClassName(classElement.getSimpleName().toString() + "_Generated");

    }

    static final class Builder extends uapi.helper.Builder<ClassMeta> {

        private String _pkgName;
        private String _className;
        private String _generatedClassName;
        private List<String> _imports = new ArrayList<>();
        private List<String> _implements = new ArrayList<>();
        private List<AnnotationMeta> _annotations = new ArrayList<>();
        private List<AnnotationMeta.Builder> _annoBuilders = new ArrayList<>();
        private List<FieldMeta> _properties = new ArrayList<>();
        private List<FieldMeta.Builder> _propBuilders = new ArrayList<>();
        private List<MethodMeta> _methods = new ArrayList<>();
        private List<MethodMeta.Builder> _methodBuilders = new ArrayList<>();

        private Builder() { }

        public Builder setPackageName(
                final String packageName
        ) throws KernelException {
            checkStatus();
            this._pkgName = packageName;
            return this;
        }

        public String getPackageName() {
            return this._pkgName;
        }

        public Builder setClassName(
                final String serviceClassName
        ) throws KernelException {
            checkStatus();
            this._className = serviceClassName;
            return this;
        }

        public String getClassName() {
            return this._className;
        }

        public Builder setGeneratedClassName(
                final String generatedClassName
        ) throws KernelException {
            checkStatus();
            this._generatedClassName = generatedClassName;
            return this;
        }

        public Builder addImport(
                final String importClassName
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notEmpty(importClassName, "importClassName");
            this._imports.add(importClassName);
            return this;
        }

        public Builder addImplement(
                final String implement
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(implement, "implement");
            return this;
        }

        public Builder addAnnotationBuilder(
                final AnnotationMeta.Builder annotationMetaBuilder
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(annotationMetaBuilder, "annotationMetaBuilder");
            this._annoBuilders.add(annotationMetaBuilder);
            return this;
        }

        public Builder addPropertyBuilder(
                final FieldMeta.Builder fieldMetaBuilder
        ) throws KernelException {
            checkStatus();
            this._propBuilders.add(fieldMetaBuilder);
            return this;
        }

        public Builder addMethodBuilder(
                final MethodMeta.Builder methodMetaBuilder
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(methodMetaBuilder, "methodMetaBuilder");
            this._methodBuilders.add(methodMetaBuilder);
            return this;
        }

        public String getGeneratedClassName() {
            return this._generatedClassName;
        }

        public MethodMeta.Builder findMethodBuilder(
                final Element methodElement,
                final BuilderContext builderContext
        ) throws KernelException {
            ArgumentChecker.notNull(methodElement, "methodElement");
            MethodMeta.Builder methodBuilder = MethodMeta.builder(methodElement, builderContext);
            List<MethodMeta.Builder> matchedBuilders = this._methodBuilders.parallelStream()
                    .filter(existing -> existing.equals(methodBuilder))
                    .collect(Collectors.toList());
            if (matchedBuilders.size() > 1) {
                throw new KernelException(
                        "Found more than one method builder for {}"
                        , methodBuilder);
            }
            if (matchedBuilders.size() == 1) {
                return matchedBuilders.get(0);
            }
            this._methodBuilders.add(methodBuilder);
            return methodBuilder;
        }

        @Override
        protected ClassMeta buildInstance(
        ) throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._pkgName, "packageName");
            ArgumentChecker.notEmpty(this._className, "className");
            ArgumentChecker.notEmpty(this._generatedClassName, "generatedClassName");
            this._annoBuilders.forEach(annoBuilder ->
                this._annotations.add(annoBuilder.buildInstance())
            );
            this._propBuilders.forEach(propBuilder ->
                this._properties.add(propBuilder.buildInstance())
            );
            this._methodBuilders.forEach(methodBuilder ->
                this._methods.add(methodBuilder.buildInstance())
            );
            return new ClassMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "ClassMeta[" +
                            "packageName={}, " +
                            "className={}, " +
                            "generatedClassName={}, " +
                            "implements={}, " +
                            "annotations={}, " +
                            "properties={}, " +
                            "methods={}]",
                    this._pkgName,
                    this._className,
                    this._generatedClassName,
                    this._implements,
                    this._annoBuilders,
                    this._propBuilders,
                    this._methodBuilders);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(this._pkgName, builder._pkgName) &&
                    Objects.equals(this._className, builder._className);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this._pkgName, this._className);
        }
    }
}
