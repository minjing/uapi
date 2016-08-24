/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

/**
 * Created by xquan on 5/3/2016.
 */
public class NamedArgumentMapping extends ArgumentMapping {

    private final String _name;

    public String getName() {
        return this._name;
    }

    public NamedArgumentMapping(
            final ArgumentFrom from,
            final String type,
            final String name
    ) throws InvalidArgumentException {
        super(from, type);
        ArgumentChecker.required(name, "name");
        this._name = name;
    }

    @Override
    protected String propertiesString() {
        return StringHelper.makeString("{},name={}", super.propertiesString(), this._name);
    }
}
