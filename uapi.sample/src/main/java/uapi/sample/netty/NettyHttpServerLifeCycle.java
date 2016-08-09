/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.netty;

import uapi.app.IAppLifecycle;
import uapi.rx.Looper;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.http.IHttpServer;

import java.util.List;

/**
 * Created by xquan on 8/9/2016.
 */
@Service({ IAppLifecycle.class })
public class NettyHttpServerLifeCycle implements IAppLifecycle {

    @Inject
    IRegistry _registry;

    private IHttpServer _httpSvr;

    @Override
    public String getAppName() {
        return "NettyHttpServer";
    }

    @Override
    public void onStarted() {
        List<IHttpServer> httpServers = this._registry.findServices(IHttpServer.class);
        this._httpSvr = Looper.from(httpServers)
                .filter(httpServer -> httpServer.getClass().getName().contains("Netty"))
                .first();
        this._httpSvr.start();
    }

    @Override
    public void onStopped() {
        this._httpSvr.stop();
    }
}
