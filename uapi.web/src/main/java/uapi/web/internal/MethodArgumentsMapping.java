/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import uapi.helper.ArgumentChecker;
import uapi.service.web.ArgumentMapping;

import java.util.*;

/**
 * Created by xquan on 5/11/2016.
 */
public final class MethodArgumentsMapping {

    private final String _name;
    private final List<ArgumentMapping> _argMappings;

    MethodArgumentsMapping(
            final String name
    ) {
        ArgumentChecker.required(name, "name");
        this._name = name;
        this._argMappings = new ArrayList<>();
    }

    void addArgumentMapping(
            final ArgumentMapping argMapping
    ) {
        ArgumentChecker.required(argMapping, "argMapping");
        this._argMappings.add(argMapping);
    }

    public String getName() {
        return this._name;
    }

    public List<ArgumentMapping> getArgumentMappings() {
        return this._argMappings;
    }

    @Override
    public String toString() {
        return "MethodArgumentsMapping[name=" + this._name + ", argMapping=" + this._argMappings + "]";
    }
}
