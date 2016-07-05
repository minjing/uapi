/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.hello;

import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.IRemoteServiceLoader;

/**
 * Created by xquan on 7/5/2016.
 */
@Service
public class HelloClient {

    @Inject(from=IRemoteServiceLoader.NAME)
    protected IHello _helloSvc;

    public String getHelloString(String title, String name) {
        return this._helloSvc.sayHello(name, title);
    }
}
