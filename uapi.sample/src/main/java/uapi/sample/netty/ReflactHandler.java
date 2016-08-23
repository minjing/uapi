/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.netty;

import uapi.service.annotation.Service;
import uapi.web.http.IHttpHandler;
import uapi.web.http.IHttpRequest;
import uapi.web.http.IHttpResponse;

/**
 * Created by xquan on 8/19/2016.
 */
@Service(IHttpHandler.class)
public class ReflactHandler implements IHttpHandler {

    @Override
    public String getUriMapping() {
        return "/a";
    }

    @Override
    public void get(IHttpRequest request, IHttpResponse response) {
        handle(request, response);
    }

    @Override
    public void put(IHttpRequest request, IHttpResponse response) {
        handle(request, response);
    }

    @Override
    public void post(IHttpRequest request, IHttpResponse response) {
        handle(request, response);
    }

    @Override
    public void delete(IHttpRequest request, IHttpResponse response) {
        handle(request, response);
    }

    private void handle(IHttpRequest request, IHttpResponse response) {
        request.params();
        request.textParam();
        response.write(request.toString());
    }
}
