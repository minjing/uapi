/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.hello;

import uapi.app.IAppLifecycle;
import uapi.sample.ConfigurableService;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.http.IHttpServer;

/**
 * Created by xquan on 4/5/2016.
 */
@Service({ IAppLifecycle.class })
public class HelloAppServerLifeCycle implements IAppLifecycle {

    @Inject
    protected IRegistry _registry;

    private IHttpServer _httpSvr;

    @Override
    public String getAppName() {
        return "HelloAppServer";
    }

    @Override
    public void onStarted() {
        this._registry.findService(ConfigurableService.class);
        this._httpSvr = this._registry.findService(IHttpServer.class);
        this._httpSvr.start();
    }

    @Override
    public void onStopped() {
        this._httpSvr.stop();
    }
}
