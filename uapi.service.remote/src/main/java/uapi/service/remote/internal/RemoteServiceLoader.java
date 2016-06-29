/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal;

import uapi.KernelException;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.service.IRegistry;
import uapi.service.IServiceLoader;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.IRemoteServiceConfigurableKey;
import uapi.service.remote.IServiceDiscover;
import uapi.service.ServiceInterfaceMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceLoader used to load service remotely
 * Service Discover: Direct, Consul
 * Invocation driver: HTTP + JSON ...
 */
@Service(IServiceLoader.class)
public class RemoteServiceLoader implements IServiceLoader {

    private static final int PRIORITY   = 200;

    @Config(path=IRemoteServiceConfigurableKey.LOADER_DISCOVER)
    String _communicatorName;

    @Inject
    IRegistry _registry;

    @Inject
    ServiceInspector _svcInspector;

    @Inject
    IServiceDiscover _svcDiscover;

    @Inject
    Map<String, ICommunicator> _communicators = new HashMap<>();

    @Inject
    ProxyBuilder _proxyBuilder;

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public String getName() {
        return "Remote";
    }

    @Override
    public <T> T load(
            final String serviceId,
            final Class<?> serviceType) {
        ArgumentChecker.required(serviceId, "serviceId");
        ICommunicator communicator = this._communicators.get(this._communicatorName);
        if (communicator == null) {
            throw new KernelException("No communicator named - {}", this._communicatorName);
        }
        ServiceInterfaceMeta svcIntfMeta = this._svcInspector.inspect(serviceId);
        svcIntfMeta = this._svcDiscover.discover(svcIntfMeta);
        return (T) this._proxyBuilder.build(svcIntfMeta);
    }
}
