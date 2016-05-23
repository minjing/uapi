package uapi.service.remote.internal;

import uapi.config.annotation.Config;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.IRemoteServiceConfigurableKey;
import uapi.service.remote.IServiceDiscover;
import uapi.service.remote.ServiceInterfaceMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Discover service from remote directly
 */
@Service(IServiceDiscover.class)
public class ServiceDiscover implements IServiceDiscover {

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_COMM)
    String _discoverComm;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_HOST)
    String _host;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_PORT)
    int _port;

    @Inject
    IRegistry _registry;

    @Inject
    Map<String, ICommunicator> _drivers = new HashMap<>();

//    @Override
//    public ICommunicator getInvocationDriver(
//            final String serviceId) {
//        ArgumentChecker.required(serviceId, "serviceId");
//        ArgumentChecker.required(this._host, "host");
//        ArgumentChecker.required(this._port, "port");
//        ArgumentChecker.required(this._driver, "driver");
//        ICommunicator driver = this._communicators.get(this._driver);
//        if (driver == null) {
//            throw new KernelException("No driver is named - " + this._driver);
//        }
//        return driver;
//    }

    @Override
    public Object discover(ServiceInterfaceMeta serviceInterfaceMeta) {
        return null;
    }
}
