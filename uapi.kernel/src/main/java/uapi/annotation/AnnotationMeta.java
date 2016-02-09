package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public final class AnnotationMeta {

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

    public static final Builder builder = new Builder();

    public static final class Builder extends uapi.helper.Builder<AnnotationMeta> {

        private String _name;
        private List<ArgumentMeta> _args = new ArrayList<>();

        public Builder setName(
                final String name
        ) {
            this._name = name;
            return this;
        }

        public Builder addArgument(
                final ArgumentMeta argumentMeta
        ) throws InvalidArgumentException {
            ArgumentChecker.notNull(argumentMeta, "argumentMeta");
            this._args.add(argumentMeta);
            return this;
        }

        @Override
        protected AnnotationMeta buildInstance()
                throws InvalidArgumentException {
            ArgumentChecker.isEmpty(this._name, "name");
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
