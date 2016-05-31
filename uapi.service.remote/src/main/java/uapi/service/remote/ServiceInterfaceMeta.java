/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote;

import rx.Observable;
import uapi.helper.ArgumentChecker;

import java.util.*;

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
        Observable.from(svcMetas).subscribe(svcMeta -> this._svcMetas.put(svcMeta.getName(), svcMeta));
    }

    public String getInterfaceId() {
        return this._intfId;
    }

    public ServiceMeta getService(String serviceName, String... argumentTypes) {
        ArgumentChecker.required(serviceName, "serviceName");
        ArgumentChecker.required(argumentTypes, "argumentType");
        return null;
    }

    public Collection<ServiceMeta> getServices() {
        return this._svcMetas.values();
    }

    public void updateServiceMetas(List<ServiceMeta> serviceMetas) {
        Observable.from(serviceMetas)
                .subscribe(serviceMeta -> {

                });
    }
}
