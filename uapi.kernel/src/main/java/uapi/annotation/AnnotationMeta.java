/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class AnnotationMeta {

    public static final String OVERRIDE     = "Override";

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

    public static class Builder extends uapi.helper.Builder<AnnotationMeta> {

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
