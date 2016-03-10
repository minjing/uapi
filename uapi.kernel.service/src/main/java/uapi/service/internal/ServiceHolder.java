package uapi.service.internal;

import rx.Observable;
import uapi.KernelException;
import uapi.ThreadSafe;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.injector.IInjectable;
import uapi.injector.Injection;
import uapi.service.IInitial;
import uapi.service.IServiceFactory;

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
    private boolean _inited = false;

    ServiceHolder(final Object service, String serviceId) {
        this(service, serviceId, new String[0]);
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

    Object getService() {
        return this._svc;
    }

    void setDependency(ServiceHolder service) {
        ArgumentChecker.notNull(service, "service");
        if (! service.isResolved()) {
            throw new KernelException("The service {} is not resolved", service._svcId);
        }
        if (! this._dependencies.containsKey(service._svcId)) {
            throw new KernelException("The service {} does not depend on service {}", this._svcId, service._svcId);
        }
        this._dependencies.put(service._svcId, service);
    }

    boolean isDependsOn(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        return this._dependencies.containsKey(serviceId);
    }

    boolean isResolved() {
        Optional<Map.Entry<String, ServiceHolder>> unresolvedSvc =
                this._dependencies.entrySet().stream().filter(entry -> entry.getValue() == null).findFirst();
        return ! unresolvedSvc.isPresent();
    }

    void initService() {
        if (this._inited) {
            return;
        }
        if (! isResolved()) {
            throw new KernelException("Unresolved service can't be initialized");
        }
        if (this._dependencies.size() > 0) {
            if (this._svc instanceof IInjectable) {
                Observable.from(this._dependencies.values())
                        .subscribe(dependency -> {
                            Object injectedSvc = dependency._svc;
                            if (dependency._svc instanceof IServiceFactory) {
                                injectedSvc = ((IServiceFactory) dependency._svc).createService(this._svc);
                            }
                            ((IInjectable) this._svc).injectObject(new Injection(dependency.getId(), injectedSvc));
                        });
            } else {
                throw new KernelException("The service {} does not implement IInjectable interface so it can't inject any dependencies");
            }
        }
        if (this._svc instanceof IInitial) {
            ((IInitial) this._svc).init();
            this._inited = true;
        }
    }

    @Override
    public String toString() {
        return StringHelper.makeString("Service[id={}, type={}, dependencies={}]",
                this._svcId, this._svc.getClass().getName(), this._dependencies);
    }
}
