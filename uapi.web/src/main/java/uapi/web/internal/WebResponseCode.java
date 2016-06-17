/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import uapi.service.MessageLoader;
import uapi.service.ResponseCode;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Define common web response code and message key mapping
 */
@Service
public class WebResponseCode extends ResponseCode {

    @Inject
    MessageLoader _msgLoader;

    @Init
    @Override
    public void init() {
        super.init();
    }

    @Override
    protected MessageLoader getMessageLoader() {
        return this._msgLoader;
    }
}
