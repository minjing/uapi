/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http;

/**
 * Indicate happen internal server error when process HTTP request
 */
public class InternalServerException extends HttpException {

    public InternalServerException(String message, Object... arguments) {
        super(HttpResponseStatus.INTERNAL_SERVER_ERROR, message, arguments);
    }

    public InternalServerException(Throwable t) {
        super(HttpResponseStatus.INTERNAL_SERVER_ERROR, t);
    }
}
