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
 * Indicate the HTTP request contains invalidate parameter(s)
 */
public class BadRequestException extends HttpException {

    public BadRequestException(String invalidParameterName) {
        this("The parameter of request is invalid - {}", invalidParameterName);
    }

    public BadRequestException(String message, Object... params) {
        super(HttpResponseStatus.BAD_REQUEST, message, params);
    }
}
