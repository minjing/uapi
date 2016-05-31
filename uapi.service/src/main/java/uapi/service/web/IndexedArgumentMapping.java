/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.web;

import uapi.InvalidArgumentException;

/**
 * Created by xquan on 5/3/2016.
 */
public class IndexedArgumentMapping extends ArgumentMapping {

    private final int _idx;

    public int getIndex() {
        return this._idx;
    }

    public IndexedArgumentMapping(
            final ArgumentFrom from,
            final String type,
            final int index
    ) throws InvalidArgumentException {
        super(from, type);
        this._idx = index;
    }
}
