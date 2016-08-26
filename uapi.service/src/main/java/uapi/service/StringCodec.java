/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Service;

/**
 * A string formatter for string.
 */
@Service(IStringCodec.class)
public class StringCodec implements IStringCodec {

    public static final String NAME = "String";

    @Override
    public Object getId() {
        return NAME;
    }

    @Override
    public String decode(Object value, Class type) throws KernelException {
        ArgumentChecker.notNull(value, "value");
        ArgumentChecker.equals(type, String.class, "type");
        return value.toString();
    }

    @Override
    public Object encode(String value, Class type) throws KernelException {
        ArgumentChecker.notNull(value, "value");
        ArgumentChecker.equals(type, String.class, "type");
        return value;
    }
}
