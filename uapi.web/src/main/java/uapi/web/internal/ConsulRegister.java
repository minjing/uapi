/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import uapi.config.annotation.Config;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.IRestfulInterface;
import uapi.web.IRestfulService;
import uapi.web.IServiceRegister;
import uapi.web.IWebConfigurableKey;

/**
 * The service register for Consul
 */
@Service(IServiceRegister.class)
public class ConsulRegister implements IServiceRegister {

    public static final String NAME = "Consul";

    @Config(path=IWebConfigurableKey.RESTFUL_REG_CONSUL, parser=ConsulConfigParser.class)
    ConsulInstance _consulInst;

    @Inject
    IRegistry _registry;

    @Override
    public String getId() {
        return NAME;
    }

    @Override
    public void register(IRestfulService restfulService) {

    }

    @Override
    public void register(IRestfulInterface restfulInterface) {

    }
}
