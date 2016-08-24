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
import uapi.service.ArgumentMeta;

/**
 * Created by xquan on 5/3/2016.
 */
public class ArgumentMapping extends ArgumentMeta {

    private ArgumentFrom _from;

    public ArgumentFrom getFrom() {
        return this._from;
    }

    public ArgumentMapping(
            final String type) {
        super(type);
    }

    public ArgumentMapping(
            final ArgumentFrom from,
            final String type
    ) throws InvalidArgumentException {
        super(type);
        ArgumentChecker.required(from, "from");
        this._from = from;
    }

    @Override
    protected String propertiesString() {
        return StringHelper.makeString("{},from={}", super.propertiesString(), this._from);
    }
}
