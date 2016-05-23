package uapi.service.remote;

import uapi.helper.ArgumentChecker;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hold service interface meta information.
 */
public final class ServiceInterfaceMeta {

    private final String _intfId;
    private final Map<String, ServiceMeta> _svcMetas;

    public ServiceInterfaceMeta(
            final String interfaceId,
            final List<ServiceMeta> svcMetas) {
        ArgumentChecker.required(interfaceId, "interfaceId");
        ArgumentChecker.required(svcMetas, "svcMetas");
        this._intfId = interfaceId;
        this._svcMetas = new HashMap<>();
    }

    public String getInterfaceId() {
        return this._intfId;
    }

    public ServiceMeta getService(String serviceName, String... argumentTypes) {
        ArgumentChecker.required(serviceName, "serviceName");
        ArgumentChecker.required(argumentTypes, "argumentType");
        return null;
    }
}
