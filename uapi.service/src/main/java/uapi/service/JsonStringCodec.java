/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import com.fasterxml.jackson.jr.ob.JSON;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Service;

import java.util.List;
import java.util.Map;

/**
 * A string formatter for JSON
 */
@Service(IStringCodec.class)
public class JsonStringCodec implements IStringCodec {

    public static final String NAME = "JSON";

    @Override
    public String getId() {
        return NAME;
    }

    @Override
    public String decode(
            final Object value,
            final Class type
    ) throws KernelException {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(type, "type");
        if (type.equals(String.class) && value instanceof String) {
            return value.toString();
        }
        try {
            return JSON.std.asString(value);
        } catch (Exception ex) {
            throw new KernelException(ex);
        }
    }

    @Override
    public Object encode(
            final String value,
            final Class type
    ) throws KernelException {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(type, "type");
        if (type.equals(String.class)) {
            return value;
        }
        try {
            if (type.equals(Map.class)) {
                return JSON.std.mapFrom(value);
            } else if (type.equals(List.class)) {
                return JSON.std.listFrom(value);
            } else {
                return JSON.std.beanFrom(type, value);
            }
        } catch (Exception ex) {
            throw new KernelException(ex);
        }
    }
}
