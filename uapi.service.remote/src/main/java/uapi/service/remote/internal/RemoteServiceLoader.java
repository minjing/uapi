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

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceLoader used to load service remotely
 * Service Discover: Direct, Consul
 * Invocation driver: HTTP + JSON ...
 */
@Service(IServiceLoader.class)
public class RemoteServiceLoader implements IServiceLoader {

    @Config(path=IRemoteServiceConfigurableKey.LOADER_DISCOVER)
    String _discoverDrv;

    @Inject
    IRegistry _registry;

    @Inject
    IServiceDiscover _svcDiscover;

    @Inject
    Map<String, ICommunicator> _drivers = new HashMap<>();

    @Override
    public String getName() {
        return "Remote";
    }

    @Override
    public <T> T load(String serviceId) {
        ArgumentChecker.required(serviceId, "serviceId");
        ICommunicator driver = this._drivers.get(this._discoverDrv);
        if (driver == null) {
            throw new KernelException("No driver named - {}", this._discoverDrv);
        }
        return null;
    }
}
