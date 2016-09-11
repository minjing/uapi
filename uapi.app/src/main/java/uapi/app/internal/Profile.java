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
import uapi.service.IService;

/**
 * Created by xquan on 9/8/2016.
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
        return false;
    }

    public enum Model {
        INCLUDE("include"),
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
        SATISFY_ALL("satisfy_all"),
        SATISFI_ANY("satisfy_any");

        public static Matching parse(String value) {
            if (SATISFY_ALL._value.equalsIgnoreCase(value)) {
                return SATISFY_ALL;
            } else if (SATISFI_ANY._value.equalsIgnoreCase(value)) {
                return SATISFI_ANY;
            } else {
                throw new KernelException("The value {} can't be parsed as Matching enum");
            }
        }

        private String _value;

        Matching(String value) {
            this._value = value;
        }
    }
}
