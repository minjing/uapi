package uapi.service.remote.internal;

import rx.Observable;
import uapi.KernelException;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Discover service from remote directly
 */
@Service(IServiceDiscover.class)
public class DirectServiceDiscover implements IServiceDiscover {

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_COMM)
    String _communicatorName;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_HOST)
    String _host;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_PORT)
    int _port;

    @Inject
    IRegistry _registry;

    @Inject
    Map<String, ICommunicator> _communicators = new HashMap<>();

    @Override
    public ServiceInterfaceMeta discover(ServiceInterfaceMeta serviceInterfaceMeta) {
        ArgumentChecker.required(serviceInterfaceMeta, "serviceInterfaceMeta");
        ICommunicator communicator = this._communicators.get(this._communicatorName);
        if (communicator == null) {
            throw new KernelException("No communicator was found by name {}", this._communicatorName);
        }

        List<ServiceMeta> svcMetas = Observable.from(serviceInterfaceMeta.getServices())
                .map(this::verifyServiceMeta)
                .toList().toBlocking().first();
        return null;
    }

    /**
     * Verify service meta from remote and return corresponding service meta
     *
     * @param   svcMeta
     *          The service meta which will be verified from remote
     * @return  Corresponding service meta
     */
    private ServiceMeta verifyServiceMeta(ServiceMeta svcMeta) {
        return null;
    }
}
