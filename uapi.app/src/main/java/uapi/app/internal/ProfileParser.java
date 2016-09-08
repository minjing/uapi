/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import uapi.config.IConfigValueParser;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * Created by xquan on 9/8/2016.
 */
@Service
@Tag("Profile")
public class ProfileParser implements IConfigValueParser {

    @Override
    public String getName() {
        return ProfileParser.class.getName();
    }

    @Override
    public boolean isSupport(String inType, String outType) {
        return false;
    }

    @Override
    public <T> T parse(Object value) {
        return null;
    }
}
