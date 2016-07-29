/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal;

import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.ArgumentMeta;
import uapi.service.ServiceInterfaceMeta;
import uapi.service.ServiceMeta;
import uapi.service.annotation.Service;
import uapi.web.ArgumentMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * The ServiceInspector inspect specific service interface by its id
 * and generate service interface meta class
 */
@Service
class ServiceInspector {

    ServiceInterfaceMeta inspect(final String serviceId, final Class<?> serviceType) {
        ArgumentChecker.required(serviceType, "serviceType");

        if (! serviceType.isInterface()) {
            throw new KernelException("The remote service type {} must be an interface", serviceType);
        }

        List<ServiceMeta> svcMetas = Observable.from(serviceType.getMethods())
                .map(method -> parseServiceMeta(method))
                .toList().toBlocking().first();
        return new ServiceInterfaceMeta(serviceId, serviceType, svcMetas);
    }

    private ServiceMeta parseServiceMeta(Method method) {
        String name = method.getName();
        String returnType = method.getReturnType().getCanonicalName();
        List<ArgumentMeta> argMappings = Observable.from(method.getParameters())
                .map(parameter -> (ArgumentMeta) parseArgumentMeta(parameter))
                .toList().toBlocking().first();
        return new ServiceMeta(name, returnType, argMappings);
    }

    public ArgumentMapping parseArgumentMeta(Parameter parameter) {
        String paramType = parameter.getType().getCanonicalName();
        return new ArgumentMapping(paramType);
    }
}
