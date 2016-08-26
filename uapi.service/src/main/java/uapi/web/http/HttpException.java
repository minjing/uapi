/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http;

import uapi.KernelException;

/**
 * Indicate a http request cause an exception
 */
public class HttpException extends KernelException {

    private final HttpResponseStatus _resStatus;

    public HttpException(HttpResponseStatus responseStatus, String message, Object... arguments) {
        super(message, arguments);
        this._resStatus = responseStatus;
    }

    public HttpException(HttpResponseStatus responseStatus, Throwable t) {
        super(t);
        this._resStatus = responseStatus;
    }

    public HttpResponseStatus getStatus() {
        return this._resStatus;
    }
}
