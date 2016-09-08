/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import uapi.service.IService;

/**
 * Created by xquan on 9/8/2016.
 */
class EmptyProfile implements IProfile {

    static final String NAME    = "EmptyProfile";

    @Override
    public boolean isAllow(IService service) {
        return true;
    }
}
