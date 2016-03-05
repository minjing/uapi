package uapi.service.internal;

import uapi.KernelException;
import uapi.ThreadSafe;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by min on 16/2/29.
 */
@ThreadSafe
final class ServiceHolder {

    private final Object _svc;
    private final String _svcId;
    private final Map<String, ServiceHolder> _dependencies;

    ServiceHolder(final Object service, String serviceId) {
        this(service, serviceId, CollectionHelper.empty());
    }

    ServiceHolder(final Object service, String serviceId, String[] dependencies) {
        ArgumentChecker.notNull(service, "service");
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        ArgumentChecker.notNull(dependencies, "dependencies");
        this._svc = service;
        this._svcId = serviceId;
        this._dependencies = new ConcurrentHashMap<>();
        Stream.of(dependencies).forEach(dependency -> this._dependencies.put(dependency, null));
    }

    String getId() {
        return this._svcId;
    }

    boolean setDependency(ServiceHolder service) {
        ArgumentChecker.notNull(service, "service");
        if (! service.isResolved()) {
            throw new KernelException("The service {} is not resolved", service._svcId);
        }
        if (! this._dependencies.containsKey(service._svcId)) {
            throw new KernelException("The service {} does not depend on service {}", this._svcId, service._svcId);
        }
        this._dependencies.put(service._svcId, service);
        return isResolved();
    }

//    void setDependency(String serviceId, ServiceHolder service) {
//        if (! service.isResolved()) {
//            throw new KernelException("The service {} is not resolved", service._svcId);
//        }
//        this._dependencies.put(serviceId, service);
//    }

    boolean dependsOn(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        return this._dependencies.containsKey(serviceId);
    }

    boolean isResolved() {
        Optional<Map.Entry<String, ServiceHolder>> unresolvedSvc =
                this._dependencies.entrySet().stream().filter(entry -> entry.getValue() == null).findFirst();
        return ! unresolvedSvc.isPresent();
    }
}
