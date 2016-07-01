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
import uapi.service.ArgumentMeta;
import uapi.service.MethodMeta;
import uapi.service.ServiceMeta;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.IServiceInterfaceProxy;
import uapi.service.ServiceInterfaceMeta;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build service proxy based on service interface meta
 */
@Service
public class ProxyBuilder {

    @Inject
    Map<String, ICommunicator> _communicators = new HashMap<>();

    IServiceInterfaceProxy build(ServiceInterfaceMeta meta) {
        Class<?> svcIntfType = meta.getInterfaceType();
        return (IServiceInterfaceProxy) Proxy.newProxyInstance(
                svcIntfType.getClassLoader(),
                new Class<?>[] { svcIntfType, IServiceInterfaceProxy.class },
                new ServiceProxy(meta));
    }

    private final class ServiceProxy implements InvocationHandler, IServiceInterfaceProxy {

        private final ServiceInterfaceMeta _svcIntfMeta;

        private ServiceProxy(final ServiceInterfaceMeta svcIntfMeta) {
            this._svcIntfMeta = svcIntfMeta;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ServiceMeta svcMeta = Looper.from(this._svcIntfMeta.getServices())
                    .filter(methodMeta -> isSame(methodMeta, method))
                    .first();
            if (svcMeta == null) {
                throw new KernelException("Can't find method {} in service interface {}", method, this._svcIntfMeta);
            }
            ICommunicator communicator = ProxyBuilder.this._communicators.get(this._svcIntfMeta.getCommunicatorName());
            if (communicator == null) {
                throw new KernelException("No communicator named {} for service interface {}", this._svcIntfMeta.getCommunicatorName(), this._svcIntfMeta);
            }
            return communicator.request(svcMeta, args);
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

        @Override
        public void setCommunicators(List<ICommunicator> communicator) {

        }

        @Override
        public void setMeta(ServiceInterfaceMeta meta) {

        }
    }
}
