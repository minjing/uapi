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
 * A resolver used to encode integer to String and encode string to integer
 */
@Deprecated
@Service(IStringResolver.class)
public class IntegerStringResolver implements IStringResolver<Integer> {

    @Override
    public String getId() {
        return Integer.class.getCanonicalName();
    }

    @Override
    public String decode(Integer value, String formatterName) {
        return String.valueOf(value);
    }

    @Override
    public Integer encode(String value, String formatterName) {
        return Integer.parseInt(value);
    }
}
