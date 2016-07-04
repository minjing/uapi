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
import uapi.rx.Looper;
import uapi.service.*;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.IServiceInterfaceProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Build service proxy based on service interface meta
 */
@Service
public class ProxyBuilder {

    @Inject
    Map<String, ICommunicator> _communicators = new HashMap<>();

    private final List<IServiceInterfaceProxy> _proxyCache = new LinkedList<>();

    Object build(ServiceInterfaceMeta meta) {
        Class<?> svcIntfType = meta.getInterfaceType();
        ICommunicator communicator = this._communicators.get(meta.getCommunicatorName());
        if (communicator == null) {
            throw new KernelException("No communicator named {} for service interface {}", meta.getCommunicatorName(), meta);
        }
        IServiceInterfaceProxy proxy = Looper.from(this._proxyCache)
                .filter(svcProxy -> svcProxy.getMeta() == meta)
                .first(null);
        if (proxy != null) {
            return proxy;
        }
        proxy = (IServiceInterfaceProxy) Proxy.newProxyInstance(
                svcIntfType.getClassLoader(),
                new Class<?>[] { svcIntfType, IServiceInterfaceProxy.class },
                new ServiceProxy(meta, communicator));
        this._proxyCache.add(proxy);
        return proxy;
    }

    private final class ServiceProxy implements InvocationHandler {

        private final ServiceInterfaceMeta _svcIntfMeta;
        private final ICommunicator _communicator;

        private ServiceProxy(final ServiceInterfaceMeta svcIntfMeta, final ICommunicator communicator) {
            this._svcIntfMeta = svcIntfMeta;
            this._communicator = communicator;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getCommunicator") && args.length == 0) {
                return this._communicator;
            }
            if (method.getName().equals("getMeta") && args.length == 0) {
                return this._svcIntfMeta;
            }
            ServiceMeta svcMeta = Looper.from(this._svcIntfMeta.getServices())
                    .filter(methodMeta -> isSame(methodMeta, method))
                    .first();
            return this._communicator.request(svcMeta, args);
        }

        private boolean isSame(MethodMeta methodMeta, Method method) {
            if (! methodMeta.getName().equals(method.getName())) {
                return false;
            }
            List<ArgumentMeta> argMetas = methodMeta.getArgumentMappings();
            Parameter[] params = method.getParameters();
            if (argMetas.size() != params.length) {
                return false;
            }
            for (int i = 0; i < argMetas.size(); i++) {
                if (! argMetas.get(i).getType().equals(params[i].getType().getName())) {
                    return false;
                }
            }
            return true;
        }
    }
}
