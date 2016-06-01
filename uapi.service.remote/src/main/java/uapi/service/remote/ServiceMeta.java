/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.service.web.ArgumentMapping;

import java.util.Collections;
import java.util.List;

/**
 * Hold remote service meta information
 */
public class ServiceMeta {

    private final String _name;
    private final String _returnTypeName;
    private List<ArgumentMapping> _argMappings;

    public ServiceMeta(
            final String name,
            final String returnTypeName,
            final List<ArgumentMapping> argMappings) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(returnTypeName, "valueParserName");
        ArgumentChecker.required(argMappings, "argMappings");
        this._name = name;
        this._returnTypeName = returnTypeName;
        this._argMappings = Collections.unmodifiableList(argMappings);
    }

    public String getName() {
        return this._name;
    }

    public String getReturnTypeName() {
        return this._returnTypeName;
    }

    public List<ArgumentMapping> getArgumentMappings() {
        return this._argMappings;
    }

    public boolean isSame(ServiceMeta other) {
        if (other == null) {
            return false;
        }
        if (! this._name.equals(other._name)) {
            return false;
        }
        if (! this._returnTypeName.equals(other._name)) {
            return false;
        }
        if (this._argMappings.size() != other._argMappings.size()) {
            return false;
        }
        for (int i = 0; i < this._argMappings.size(); i++) {
            if (! this._argMappings.get(i).isSameType(other._argMappings.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("name={},returnTypeName={},argMappings={}",
                this._name, this._returnTypeName, CollectionHelper.asString(this._argMappings));
    }
}
