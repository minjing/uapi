package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.annotation.internal.BuilderContext;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import java.util.*;
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

    public List<FieldMeta> getFields() {
        return this._builder._fields;
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

    public static final class Builder extends uapi.helper.Builder<ClassMeta> {

        private String _pkgName;
        private String _className;
        private String _generatedClassName;
        private List<String> _imports = new ArrayList<>();
        private List<String> _implements = new ArrayList<>();
        private List<AnnotationMeta> _annotations = new ArrayList<>();
        private List<AnnotationMeta.Builder> _annoBuilders = new ArrayList<>();
        private List<FieldMeta> _fields = new ArrayList<>();
        private List<FieldMeta.Builder> _fieldBuilders = new ArrayList<>();
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
            if (! this._implements.contains(implement)) {
                this._implements.add(implement);
            }
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

        public Builder addFieldBuilder(
                final FieldMeta.Builder fieldMetaBuilder
        ) throws KernelException {
            checkStatus();
            this._fieldBuilders.add(fieldMetaBuilder);
            return this;
        }

        public Builder addMethodBuilder(
                final MethodMeta.Builder methodMetaBuilder
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(methodMetaBuilder, "methodMetaBuilder");
            if (! this._methodBuilders.contains(methodMetaBuilder)) {
                this._methodBuilders.add(methodMetaBuilder);
            }
            return this;
        }

        public String getGeneratedClassName() {
            return this._generatedClassName;
        }

        public MethodMeta.Builder findMethodBuilder(
                final Element methodElement,
                final IBuilderContext builderContext
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

        public List<MethodMeta.Builder> findSetterBuilders() {
            List<MethodMeta.Builder> setters = new ArrayList<>();
            this._methodBuilders.forEach(methodBuilder -> {
                if (methodBuilder.isSetter()) {
                    setters.add(methodBuilder);
                }
            });
            return setters;
        }

        public List<MethodMeta.Builder> findMethodBuilders(
                final String methodName
        ) throws InvalidArgumentException {
            ArgumentChecker.notEmpty(methodName, "methodName");
            return this._methodBuilders.parallelStream()
                    .filter(methodBuilder -> methodBuilder.getName().equals(methodName))
                    .collect(Collectors.toList());
        }

        public MethodMeta.Builder findMethodBuilder(
                final MethodMeta.Builder methodBuilder
        ) throws InvalidArgumentException {
            ArgumentChecker.notNull(methodBuilder, "methodBuilder");
            List<MethodMeta.Builder> matchedMethods = this._methodBuilders.parallelStream()
                    .filter(existing -> existing.equals(methodBuilder))
                    .collect(Collectors.toList());
            if (matchedMethods.size() == 0) {
                return null;
            }
            if (matchedMethods.size() == 1) {
                return matchedMethods.get(0);
            }
            throw new KernelException("Found not only one method builder - {}" + matchedMethods);
        }

        public MethodMeta.Builder addMethodBuilderIfAbsent(
                final MethodMeta.Builder methodBuilder
        ) throws InvalidArgumentException {
            checkStatus();
            MethodMeta.Builder foundBuilder = findMethodBuilder(methodBuilder);
            if (foundBuilder == null) {
                addMethodBuilder(methodBuilder);
                return methodBuilder;
            } else {
                return foundBuilder;
            }
        }

        @Override
        protected void validation() throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._pkgName, "packageName");
            ArgumentChecker.notEmpty(this._className, "className");
            ArgumentChecker.notEmpty(this._generatedClassName, "generatedClassName");
            this._annoBuilders.forEach(AnnotationMeta.Builder::validation);
            this._fieldBuilders.forEach(FieldMeta.Builder::validation);
            this._methodBuilders.forEach(MethodMeta.Builder::validation);
        }

        @Override
        protected void initProperties() {
            this._annoBuilders.forEach(annoBuilder -> {
                annoBuilder.initProperties();
                this._annotations.add(annoBuilder.createInstance());
            });
            this._fieldBuilders.forEach(fieldBuilder -> {
                fieldBuilder.initProperties();
                this._fields.add(fieldBuilder.createInstance());
            });
            this._methodBuilders.forEach(methodBuilder -> {
                methodBuilder.initProperties();
                this._methods.add(methodBuilder.createInstance());
            });
        }

        @Override
        protected ClassMeta createInstance() {
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
                            "fields={}, " +
                            "fieldBuilders={}, " +
                            "methods={}]",
                    this._pkgName,
                    this._className,
                    this._generatedClassName,
                    this._implements,
                    this._annoBuilders,
                    this._fields,
                    this._fieldBuilders,
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
