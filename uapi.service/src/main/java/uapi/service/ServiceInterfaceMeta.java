/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;

import java.util.*;

/**
 * Hold service interface meta information.
 */
public final class ServiceInterfaceMeta {

    private final String _intfId;
    private final Class<?> _intfType;
    private final Multimap<String, ServiceMeta> _svcMetas;
    private String _commName;

    public ServiceInterfaceMeta(
            final String interfaceId,
            final Class<?> interfaceType,
            final List<ServiceMeta> svcMetas) {
        ArgumentChecker.required(interfaceId, "interfaceId");
        ArgumentChecker.required(interfaceType, "interfaceType");
        ArgumentChecker.required(svcMetas, "svcMetas");
        this._intfId = interfaceId;
        this._intfType = interfaceType;
        this._svcMetas = LinkedListMultimap.create();
        Observable.from(svcMetas).subscribe(svcMeta -> this._svcMetas.put(svcMeta.getName(), svcMeta));
    }

    public String getInterfaceId() {
        return this._intfId;
    }

    public Class<?> getInterfaceType() {
        return this._intfType;
    }

    public void updateServiceMetas(List<ServiceMeta> svcMetas) {
        ArgumentChecker.required(svcMetas, "svcMetas");
        if (this._svcMetas.size() != svcMetas.size()) {
            throw new KernelException("Defined different service count, expect {}, actually {}",
                    this._svcMetas.size(), svcMetas.size());
        }
        // Check updated service metas are matched which exiting service metas
        for (ServiceMeta svcMeta : svcMetas) {
            ArgumentChecker.notNull(svcMeta, "svcMeta");
            String name = svcMeta.getName();
            Collection<ServiceMeta> existings = this._svcMetas.get(name);
            if (existings == null || existings.size() == 0) {
                throw new KernelException("No service meta was found by name - {}", name);
            }
            boolean isMatched = false;
            for (ServiceMeta existing : existings) {
                if (existing.isSame(svcMeta)) {
                    isMatched = true;
                    break;
                }
            }
            if (! isMatched) {
                throw new KernelException("Can't found matched service meta for {}", svcMeta);
            }
        }
        // Clean all existing service metas and put updated service meta
        this._svcMetas.clear();
        Observable.from(svcMetas).subscribe(svcMeta -> this._svcMetas.put(svcMeta.getName(), svcMeta));
    }

    public Collection<ServiceMeta> getServices() {
        return this._svcMetas.values();
    }

    public void setCommunicatorName(String communicatorName) {
        ArgumentChecker.required(communicatorName, "communicatorName");
        this._commName = communicatorName;
    }

    public String getCommunicatorName() {
        return this._commName;
    }
}
