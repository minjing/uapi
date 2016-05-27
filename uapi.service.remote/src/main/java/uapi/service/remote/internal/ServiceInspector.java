package uapi.service.remote.internal;

import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.remote.ServiceInterfaceMeta;
import uapi.service.remote.ServiceMeta;
import uapi.service.web.ArgumentMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * The ServiceInspector inspect specific service interface by its id
 * and generate service interface meta class
 */
class ServiceInspector {

    ServiceInterfaceMeta inspect(final String serviceId) {
        ArgumentChecker.required(serviceId, "serviceId");

        Class<?> svcType;
        try {
            svcType = Class.forName(serviceId);
        } catch (ClassNotFoundException ex) {
            throw new KernelException(ex);
        }
        if (! svcType.isInterface()) {
            throw new KernelException("The remote service id {} must be an interface", serviceId);
        }

        List<ServiceMeta> svcMetas = Observable.from(svcType.getMethods())
                .map(method -> parseServiceMeta(method))
                .toList().toBlocking().first();
        return new ServiceInterfaceMeta(svcType.getCanonicalName(), svcMetas);
    }

    private ServiceMeta parseServiceMeta(Method method) {
        String name = method.getName();
        String returnType = method.getReturnType().getCanonicalName();
        List<ArgumentMapping> argMappings = Observable.from(method.getParameters())
                .map(parameter -> parseArgumentMeta(parameter))
                .toList().toBlocking().first();
        return new ServiceMeta(name, returnType, argMappings);
    }

    public ArgumentMapping parseArgumentMeta(Parameter parameter) {
        String paramType = parameter.getType().getCanonicalName();
        return new ArgumentMapping(paramType);
    }
}
