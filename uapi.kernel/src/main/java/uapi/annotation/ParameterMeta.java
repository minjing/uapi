/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A meta object for a formal parameter
 */
public class ParameterMeta {

    private final Builder _builder;

    public ParameterMeta(
            final Builder builder
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(builder, "builder");
        this._builder = builder;
    }

    public String getName() {
        return this._builder._name;
    }

    public String getType() {
        return this._builder._type;
    }

    public String getModifiers() {
        return CollectionHelper.asString(this._builder._modifiers, " ");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(
            final Element parameterElement,
            final IBuilderContext builderContext
    ) throws KernelException {
        ArgumentChecker.notNull(parameterElement, "parameterElement");
        ArgumentChecker.notNull(builderContext, "builderContext");
        if (parameterElement.getKind() != ElementKind.PARAMETER) {
            throw new KernelException(
                    "The element is not a parameter element - {}",
                    parameterElement.getSimpleName().toString());
        }

        return new Builder()
                .setName(parameterElement.getSimpleName().toString())
                .setType(parameterElement.asType().toString());
    }

    public static class Builder extends uapi.helper.Builder<ParameterMeta> {

        private String _name;
        private String _type;
        private List<Modifier> _modifiers = new ArrayList<>();

        protected Builder() { }

        public Builder setName(
                final String name
        ) throws KernelException {
            checkStatus();
            this._name = name;
            return this;
        }

        public String getName() {
            return this._name;
        }

        public Builder setType(
                final String type
        ) throws KernelException {
            checkStatus();
            this._type = type;
            return this;
        }

        public String getType() {
            return this._type;
        }

        public Builder addModifier(Modifier modifier) {
            checkStatus();
            ArgumentChecker.notNull(modifier, "modifier");
            this._modifiers.add(modifier);
            return this;
        }

        @Override
        protected void validation() throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._name, "name");
            ArgumentChecker.notEmpty(this._type, "type");
        }

        @Override
        protected void initProperties() { }

        @Override
        protected ParameterMeta createInstance() {
            return new ParameterMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "ParameterMeta[" +
                            "modifiers={}, " +
                            "name={}, " +
                            "type={}, ",
                    this._modifiers, this._name, this._type
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(_name, builder._name) &&
                    Objects.equals(_type, builder._type) &&
                    CollectionHelper.equals(_modifiers, builder._modifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_name, _type);
        }
    }
}
