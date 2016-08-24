/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful;

import uapi.KernelException;

/**
 * Indicate where is the argument from.
 */
public enum ArgumentFrom {

    /**
     * The argument can be retrieved form HTTP header
     */
    Header,

    /**
     * The argument value can be retrieved from HTTP request query parameters, post parameter
     */
    Param,

    /**
     * The argument value can be retrieved from HTTP request URI
     */
    Uri;

    public static ArgumentFrom parse(String from) {
        if (Header.name().equalsIgnoreCase(from)) {
            return Header;
        } else if (Param.name().equalsIgnoreCase(from)) {
            return Param;
        } else if (Uri.name().equalsIgnoreCase(from)) {
            return Uri;
        } else {
            throw new KernelException("No ArgumentFrom can be mapped to {}", from);
        }
    }
}
