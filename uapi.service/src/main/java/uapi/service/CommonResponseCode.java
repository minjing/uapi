/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Define common usage response code and message key mapping
 */
@Service
public class CommonResponseCode extends ResponseCode {

    public static final String SUCCESS  = "000";
    public static final String FAILURE  = "001";

    public static final String MSG_KEY_SUCCESS  = "success";
    public static final String MSG_KEY_FAILURE  = "failure";

    @Inject
    MessageLoader _msgLoader;

    @Init
    @Override
    public void init() {
        super.init();

        addCodeMessageKeyMapping(SUCCESS, MSG_KEY_SUCCESS);
        addCodeMessageKeyMapping(FAILURE, MSG_KEY_FAILURE);
    }

    @Override
    protected MessageLoader getMessageLoader() {
        return this._msgLoader;
    }
}
