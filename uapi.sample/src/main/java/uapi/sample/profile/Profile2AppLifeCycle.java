/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample.profile;

import uapi.app.IAppLifecycle;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 9/14/2016.
 */
@Service(IAppLifecycle.class)
public class Profile2AppLifeCycle implements IAppLifecycle {

    @Inject
    IRegistry _registry;

    @Override
    public String getAppName() {
        return "Profile2EnabledApp";
    }

    @Override
    public void onStarted() {
        assert this._registry.findServices(Profile1Service.class) == null;
        assert this._registry.findServices(Profile2Service.class) != null;
        assert this._registry.findServices(Profile3Service.class) != null;
        assert this._registry.findServices(Profile12Service.class) != null;
        assert this._registry.findServices(Profile23Service.class) != null;
    }

    @Override
    public void onStopped() {

    }
}
