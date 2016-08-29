/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import java.util.List;

/**
 * Hold remote service meta information
 */
public class ServiceMeta extends MethodMeta {

    private String _id;

    public ServiceMeta(
            final String name,
            final String returnTypeName
    ) {
        this(name, returnTypeName, null);
    }

    public ServiceMeta(
            final String name,
            final String returnTypeName,
            final List<ArgumentMeta> argMappings) {
        super(name, returnTypeName, argMappings);
    }

    public String getId() {
        return this._id;
    }

    public void setId(final String id) {
        ArgumentChecker.required(id, "id");
        this._id = id;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("name={},returnTypeName={},argMappings={}",
                getName(), getReturnTypeName(), CollectionHelper.asString(getArgumentMetas()));
    }
}
