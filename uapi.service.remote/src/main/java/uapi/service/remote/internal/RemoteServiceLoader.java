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
import uapi.log.ILogger;
import uapi.service.IRegistry;
import uapi.service.IServiceLoader;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.IRemoteServiceConfigurableKey;
import uapi.service.remote.IRemoteServiceLoader;
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
public class RemoteServiceLoader implements IRemoteServiceLoader {

    private static final int PRIORITY   = 200;

    @Config(path=IRemoteServiceConfigurableKey.LOADER_COMM)
    protected String _communicatorName;

    @Inject
    protected IRegistry _registry;

    @Inject
    protected ILogger _logger;

    @Inject
    protected ServiceInspector _svcInspector;

    @Inject
    protected IServiceDiscover _svcDiscover;

    @Inject
    protected Map<String, ICommunicator> _communicators = new HashMap<>();

    @Inject
    protected ProxyBuilder _proxyBuilder;

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public String getId() {
        return NAME;
    }

//    @Init
//    public void init() {
//        this._registry.registerServiceLoader(this);
//    }

    @Override
    public <T> T load(
            final String serviceId,
            final Class<?> serviceType) {
        ArgumentChecker.required(serviceId, "serviceId");
        ArgumentChecker.required(serviceType, "serviceType");
        ICommunicator communicator = this._communicators.get(this._communicatorName);
        if (communicator == null) {
            throw new KernelException("No communicator named - {}", this._communicatorName);
        }
        ServiceInterfaceMeta svcIntfMeta = null;
        try {
            svcIntfMeta = this._svcInspector.inspect(serviceId, serviceType);
        } catch (Exception ex) {
            this._logger.warn(ex, "Inspect service {} failed, cause: ", serviceId);
        }
        svcIntfMeta = this._svcDiscover.discover(svcIntfMeta);
        return (T) this._proxyBuilder.build(svcIntfMeta);
    }
}
