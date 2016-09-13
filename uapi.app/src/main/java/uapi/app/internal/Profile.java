/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.service.IService;
import uapi.service.ITagged;

/**
 * A profile implementation
 */
class Profile implements IProfile {

    private String _name;
    private String[] _tags;
    private Model _model;
    private Matching _matching;

    Profile(String name, Model model, Matching matching, String[] tags) {
        ArgumentChecker.notEmpty(name, "name");
        ArgumentChecker.required(model, "model");
        ArgumentChecker.required(matching, "matching");
        ArgumentChecker.required(tags, "tags");

        this._name = name;
        this._model = model;
        this._matching = matching;
        this._tags = tags;
    }

    String getName() {
        return this._name;
    }

    Model getModel() {
        return this._model;
    }

    Matching getMatching() {
        return this._matching;
    }

    String[] getTags() {
        return this._tags;
    }

    @Override
    public boolean isAllow(IService service) {
        String[] tags = new String[0];
        if (service instanceof ITagged) {
            tags = ((ITagged) service).getTags();

        }

        if (this._model == Model.INCLUDE) {
            if (this._matching == Matching.SATISFY_ALL) {
                return CollectionHelper.isContainsAll(tags, this._tags);
            } else if (this._matching == Matching.SATISFY_ANY) {
                return CollectionHelper.isContains(tags, this._tags);
            } else {
                throw new KernelException("Unsupported matching - {}", this._matching);
            }
        } else if (this._model == Model.EXCLUDE) {
            if (this._matching == Matching.SATISFY_ALL) {
                return ! CollectionHelper.isContainsAll(tags, this._tags);
            } else if (this._matching == Matching.SATISFY_ANY) {
                return ! CollectionHelper.isContains(tags, this._tags);
            } else {
                throw new KernelException("Unsupported matching - {}", this._matching);
            }
        } else {
            throw new KernelException("Unsupported model - {}", this._model);
        }
    }

    public enum Model {
        /**
         * Include all satisfied services
         */
        INCLUDE("include"),
        /**
         * Exclude all satisfied services
         */
        EXCLUDE("exclude");

        private String _value;

        public static Model parse(String value) {
            if (INCLUDE._value.equalsIgnoreCase(value)) {
                return INCLUDE;
            } else if (EXCLUDE._value.equalsIgnoreCase(value)) {
                return EXCLUDE;
            } else {
                throw new KernelException("The value {} can't be parsed as Model enum");
            }
        }

        Model(String value) {
            this._value = value;
        }
    }

    public enum Matching {
        /**
         * All tags must be satisfied
         */
        SATISFY_ALL("satisfy-all"),
        /**
         * One of tags must be satisfied
         */
        SATISFY_ANY("satisfy-any");

        public static Matching parse(String value) {
            if (SATISFY_ALL._value.equalsIgnoreCase(value)) {
                return SATISFY_ALL;
            } else if (SATISFY_ANY._value.equalsIgnoreCase(value)) {
                return SATISFY_ANY;
            } else {
                throw new KernelException("The value {} can't be parsed as Matching enum", value);
            }
        }

        private String _value;

        Matching(String value) {
            this._value = value;
        }
    }
}
