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
 * Created by xquan on 8/18/2016.
 */
public interface IHttpResponse {

    void setVersion(HttpVersion httpVersion);

    void setStatus(HttpResponseStatus responseStatus);

    void setHeader(String key, String value);

    void setHeader(String key, Object value);

    void setHeader(String key, int value);

    void setHeader(String key, float value);

    void write(String message);

    void flush();
}
