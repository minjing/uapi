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
import uapi.log.ILogger;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 7/5/2016.
 */
@Service({ IAppLifecycle.class })
public class HelloAppClientLifeCycle implements IAppLifecycle {

    @Inject
    IRegistry _registry;

    @Inject
    ILogger _logger;

    @Override
    public String getAppName() {
        return "HelloAppClient";
    }

    @Override
    public void onStarted() {
        HelloClient client = this._registry.findService(HelloClient.class);
        this._logger.info(client.getHelloString("Mr.", "Min"));
    }

    @Override
    public void onStopped() {

    }
}
