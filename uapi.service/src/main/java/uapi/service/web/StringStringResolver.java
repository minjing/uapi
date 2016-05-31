/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.web;

import uapi.service.annotation.Service;

/**
 * A String to String resolver
 */
@Service(IStringResolver.class)
public class StringStringResolver implements IStringResolver<String> {

    @Override
    public String getId() {
        return String.class.getCanonicalName();
    }

    @Override
    public String encode(String value, String formatterName) {
        return value;
    }

    @Override
    public String decode(String value, String formatterName) {
        return value;
    }
}
