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

/**
 * A meta object for actual argument
 */
public class ArgumentMeta {

    private final Builder _builder;

    private ArgumentMeta(
            final Builder builder
    ) {
        this._builder = builder;
    }

    public String getName() {
        return this._builder._name;
    }

    public String getValue() {
        return this._builder._value;
    }

    public boolean getIsString() {
        return this._builder._isString;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends uapi.helper.Builder<ArgumentMeta> {

        private String _name;
        private String _value;
        private boolean _isString;

        protected Builder() { }

        public Builder setName(
                final String name
        ) {
            super.checkStatus();
            this._name = name;
            return this;
        }

        public Builder setValue(
                final String value
        ) {
            super.checkStatus();
            this._value = value;
            return this;
        }

        public Builder setIsString(
                final boolean isString
        ) {
            super.checkStatus();
            this._isString = isString;
            return this;
        }

        @Override
        protected void validation() throws InvalidArgumentException {
            ArgumentChecker.notEmpty(this._name, "name");
            ArgumentChecker.notNull(this._value, "value");
        }

        @Override
        protected void initProperties() { }

        @Override
        protected ArgumentMeta createInstance() {
            return new ArgumentMeta(this);
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "ArgumentMeta[name={}, value={}, isString={}]",
                    this._name, this._value, this._isString
            );
        }
    }
}
