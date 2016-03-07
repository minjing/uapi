package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public final class AnnotationMeta {

    public static final String OVERRIDE     = "Override";
    public static final String AUTO_SERVICE = "AutoService";

    private final Builder _builder;

    private AnnotationMeta(Builder builder) {
        this._builder = builder;
    }

    public String getName() {
        return this._builder._name;
    }

    public List<ArgumentMeta> getArguments() {
        return this._builder._args;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends uapi.helper.Builder<AnnotationMeta> {

        private String _name;
        private List<ArgumentMeta> _args = new ArrayList<>();
        private List<ArgumentMeta.Builder> _argBuilders = new ArrayList<>();

        public Builder setName(
                final String name
        ) {
            this._name = name;
            return this;
        }

        public Builder addArgument(
                final ArgumentMeta.Builder builder
        ) throws InvalidArgumentException {
            ArgumentChecker.notNull(builder, "builder");
            this._argBuilders.add(builder);
            return this;
        }

        @Override
        protected void validation() throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._name, "name");
        }

        @Override
        protected void initProperties() {
            this._argBuilders.forEach(argBuilder -> {
                argBuilder.initProperties();
                this._args.add(argBuilder.createInstance());
            });
        }

        @Override
        protected AnnotationMeta createInstance() {
            return new AnnotationMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "AnnotationMeta[name={}, arguments={}]",
                    this._name, this._args
            );
        }
    }
}