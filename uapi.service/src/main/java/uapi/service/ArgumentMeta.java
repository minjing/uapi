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
import uapi.helper.StringHelper;

/**
 * Created by xquan on 6/20/2016.
 */
public class ArgumentMeta {

    private final String _typeName;

    public ArgumentMeta(final String typeName) {
        ArgumentChecker.required(typeName, "typeName");
        this._typeName = typeName;
    }

    public boolean isSameType(ArgumentMeta argumentMeta) {
        ArgumentChecker.required(argumentMeta, "argumentMeta");
        return this._typeName.equals(argumentMeta._typeName);
    }

    public String getType() {
        return this._typeName;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("ArgumentMeta[{}]", propertiesString());
    }

    protected String propertiesString() {
        return StringHelper.makeString("type={}", this._typeName);
    }
}
