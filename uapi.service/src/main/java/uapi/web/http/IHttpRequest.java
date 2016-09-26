/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by xquan on 8/18/2016.
 */
public interface IHttpRequest {

    boolean isKeepAlive();

    Map<String, String> headers();

    String uri();

    HttpMethod method();

    HttpVersion version();

    Map<String, List<String>> params();

    ContentType contentType();

    Charset charset();

    <T> T objectParam(Class<T> objectType);

    String textParam();

    void saveBody(OutputStream outputStream);
}
