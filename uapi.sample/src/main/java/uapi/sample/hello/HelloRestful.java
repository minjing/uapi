/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.hello;

import uapi.helper.StringHelper;
import uapi.service.annotation.Exposure;
import uapi.service.annotation.Service;
import uapi.web.http.HttpMethod;
import uapi.web.restful.annotation.FromParam;
import uapi.web.restful.annotation.FromUri;
import uapi.web.restful.annotation.Restful;

/**
 * The restful service for hello
 */
@Service(IHello.class)
@Exposure("hello")
public class HelloRestful implements IHello {

    @Override
    @Restful(HttpMethod.GET)
    public String sayHello(
            @FromUri(0) String name,
            @FromParam("title") String title
    ) {
        return StringHelper.makeString("Hello {} {}", title, name);
    }
}
