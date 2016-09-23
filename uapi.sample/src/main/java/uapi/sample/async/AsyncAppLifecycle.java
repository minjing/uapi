/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.async;

import uapi.app.IAppLifecycle;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * Created by xquan on 9/23/2016.
 */
@Service(IAppLifecycle.class)
@Tag("Application")
public class AsyncAppLifecycle implements IAppLifecycle {

    @Inject
    protected IRegistry _registry;

    @Override
    public String getAppName() {
        return "AsyncServiceApp";
    }

    @Override
    public void onStarted() {
        AsyncServiceCall call = this._registry.findService(AsyncServiceCall.class);
        assert call != null;
        call.callService();
    }

    @Override
    public void onStopped() {

    }
}
