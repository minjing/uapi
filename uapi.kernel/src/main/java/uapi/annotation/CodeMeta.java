/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import freemarker.template.Template;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.MapHelper;
import uapi.helper.StringHelper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Code metadata used to generate codes in specific method
 */
public class CodeMeta {

    private final Builder _builder;

    private CodeMeta(
            final Builder builder
    ) {
        this._builder = builder;
    }

    public String getCode() {
        if (this._builder._temp != null) {
            StringWriter writer = new StringWriter();
            try {
                this._builder._temp.process(this._builder._model, writer);
            } catch (Exception ex) {
                throw new KernelException(ex);
            }
            return writer.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            this._builder._rawCodes.forEach(rawCode -> sb.append(rawCode));
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        return this._builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends uapi.helper.Builder<CodeMeta> {

        private Object _model;
        private Template _temp;
        private List<String> _rawCodes = new ArrayList<>();

        public Builder setModel(
                final Object model
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(model, "model");
            this._model = model;
            return this;
        }

        public Object getModel() {
            return this._model;
        }

        public Builder setTemplate(
                final Template template
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notNull(template, "template");
            this._temp = template;
            return this;
        }

        public Builder addRawCode(
                final String code
        ) throws KernelException {
            checkStatus();
            ArgumentChecker.notEmpty(code, "code");
            this._rawCodes.add(code);
            return this;
        }

        public String getTemplateSourceName() {
            return this._temp == null ? null : this._temp.getSourceName();
        }

        @Override
        protected void validation() throws InvalidArgumentException {
            if (this._temp != null) {
                ArgumentChecker.notNull(this._model, "model");
                ArgumentChecker.notNull(this._temp, "template");
            } else {
                ArgumentChecker.notZero(this._rawCodes, "rawCodes");
            }
        }

        @Override
        protected void initProperties() { }

        @Override
        protected CodeMeta createInstance() {
            return new CodeMeta(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            Builder other = (Builder) obj;
            if (this._temp != null) {
                if (other._temp == null) {
                    return false;
                }
                if (! this._temp.getSourceName().equals(other._temp.getSourceName())) {
                    return false;
                }
                if (this._model == null || other._model == null) {
                    return false;
                }
                return this._model.equals(other._model);
            } else {
                return CollectionHelper.equals(this._rawCodes, other._rawCodes);
            }
        }

        @Override
        public int hashCode() {
            int result = _model != null ? _model.hashCode() : 0;
            result = 31 * result + (_temp != null ? _temp.hashCode() : 0);
            result = 31 * result + (_rawCodes != null ? _rawCodes.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return StringHelper.makeString(
                    "CodeMeta[" +
                            "model = {}, " +
                            "template = {}, " +
                            "rawCodes = {}",
                    this._model,
                    this._temp,
                    this._rawCodes);
        }
    }
}
