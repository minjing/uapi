package uapi.service.internal;

import com.google.auto.service.AutoService;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.ThreadSafe;
import uapi.helper.ArgumentChecker;
import uapi.helper.Guarder;
import uapi.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of IRegistry
 */
@ThreadSafe
@AutoService(IService.class)
public class Registry implements IRegistry, IService, IInjectable {

    private final Lock _resolvedLock;
    private final Lock _unresolvedLock;
    private final Multimap<String, ServiceHolder> _resolvedSvcs;
    private final Multimap<String, ServiceHolder> _unresolvedSvcs;
    private final List<IWatcher> _watchers;

    public Registry() {
        this._resolvedLock = new ReentrantLock();
        this._unresolvedLock = new ReentrantLock();
        this._resolvedSvcs = LinkedListMultimap.create();
        this._unresolvedSvcs = LinkedListMultimap.create();
        this._watchers = new CopyOnWriteArrayList<>();
    }

    @Override
    public String[] getIds() {
        return new String[] { IRegistry.class.getCanonicalName() };
    }

    @Override
    public void register(
            final IService service
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(service, "service");
        registerService(service);
    }

    @Override
    public void register(
            final IService... services
    ) throws InvalidArgumentException {
        Stream.of(services).forEach(this::register);
    }

    @Override
    public void register(
            final Object object,
            final String... serviceIds
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(object, "object");
        ArgumentChecker.notEmpty(serviceIds, "serviceIds");
        registerService(object, serviceIds);
    }

    @Override
    public <T> T findService(final Class<T> serviceType) {
        ArgumentChecker.notNull(serviceType, "serviceType");
        return findService(serviceType.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findService(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        List<Object> svcs = findServices(serviceId);
        if (svcs.size() == 0) {
            return null;
        }
        if (svcs.size() == 1) {
            return (T) svcs.get(0);
        }
        throw new KernelException("Find multiple service by service id {}", serviceId);
    }

    @Override
    public <T> List<T> findServices(final Class<T> serviceType) {
        ArgumentChecker.notNull(serviceType, "serviceType");
        return findService(serviceType.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findServices(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        List<T> resolvedSvcs = new ArrayList<>();
        Observable.from(this._resolvedSvcs.values())
                .filter(svcHolder -> svcHolder.getId().equals(serviceId))
                .map(ServiceHolder::getService)
                .subscribe(svc -> resolvedSvcs.add((T) svc));
        Observable.from(this._unresolvedSvcs.values())
                .filter(svcHolder -> svcHolder.getId().equals(serviceId))
                .first()
                .subscribe(svcHolder -> {
                    throw new KernelException("Found unresolved service {}", svcHolder);
                }, t -> { /* Do nothing */ });
        return resolvedSvcs;
    }

    int getCount() {
        return getResolvedCount() + getUnresolvedCount();
    }

    int getResolvedCount() {
        return Guarder.by(this._resolvedLock).runForResult(this._resolvedSvcs::size);
//        return Executor.create().guardBy(this._resolvedLock).runForResult(() -> this._resolvedSvcs.size());
    }

    int getUnresolvedCount() {
        return Guarder.by(this._unresolvedLock).runForResult(this._unresolvedSvcs::size);
//        return Executor.create().guardBy(this._unresolvedLock).runForResult(() -> this._unresolvedSvcs.size());
    }

    private void registerService(
            final Object svc,
            final String[] svcIds) {
        Observable.from(svcIds)
                .map(svcId -> {
                    ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
                    Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
//                    Executor.create().guardBy(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
                    return svcHolder;
                })
                .doOnNext(svcHolder -> Observable.from(this._watchers).subscribe(watcher -> watcher.onRegister(svcHolder)))
                .subscribe(this::newResolvedService);
//        Stream.of(svcIds).map(svcId -> {
//            ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
//            Executor.create().guardBy(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
//            return svcHolder;
//        }).forEach(this::newResolvedService);
    }

    private void registerService(
            final IService svc) {
        String[] svcIds = svc.getIds();
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }

        final String[] dependencyIds = svc instanceof IInjectable ? ((IInjectable) svc).getDependentIds() : new String[0];
        if (dependencyIds == null || dependencyIds.length == 0) {
            Observable.from(svcIds)
                    .map(svcId -> {
                        ServiceHolder svcHolder = new ServiceHolder(svc, svcId, dependencyIds);
                        Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
                        return svcHolder;
                    })
                    .doOnNext(svcHolder -> Observable.from(this._watchers).subscribe(watcher -> watcher.onRegister(svcHolder)))
                    .forEach(this::newResolvedService);
        } else {
            Observable.from(svcIds)
                    .map(svcId -> new ServiceHolder(svc, svcId, dependencyIds))
                    .doOnNext(svcHolder -> Observable.from(this._watchers).subscribe(watcher -> watcher.onRegister(svcHolder)))
                    .forEach(svcHolder -> {
                        if (svcHolder.isResolved()) {
                            Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcHolder.getId(), svcHolder));
                            newResolvedService(svcHolder);
                        } else {
                            Guarder.by(this._unresolvedLock).run(() -> this._unresolvedSvcs.put(svcHolder.getId(), svcHolder));
                        }
                    });
        }
    }

    private void newResolvedService(final ServiceHolder resolvedService) {
        final String resolvedSvcId = resolvedService.getId();

        resolvedService.initService();
        Observable.from(this._watchers).subscribe(watcher -> watcher.onResolved(resolvedService));

        List<ServiceHolder> dependSvcs = Guarder.by(this._unresolvedLock).runForResult(
                () -> this._unresolvedSvcs.values().stream()
                        .filter(serviceHolder -> serviceHolder.isDependsOn(resolvedSvcId)).collect(Collectors.toList()));
        Observable.from(dependSvcs)
                .doOnNext(serviceHolder -> serviceHolder.setDependency(resolvedService))
                .filter(ServiceHolder::isResolved)
                .doOnNext(serviceHolder -> {
                    Guarder.by(this._unresolvedLock).run(() -> this._unresolvedSvcs.remove(serviceHolder.getId(), serviceHolder));
                    Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(serviceHolder.getId(), serviceHolder));
                })
                .forEach(this::newResolvedService);
    }

    @Override
    public void injectObject(
            final Injection injection
    ) throws InvalidArgumentException, KernelException {
        ArgumentChecker.notNull(injection, "injection");
        if (IWatcher.class.getName().equals(injection.getId()) && injection.getObject() instanceof IWatcher) {
            this._watchers.add((IWatcher) injection.getObject());
            return;
        }
        throw new InvalidArgumentException("The Registry does not depends on service {}", injection);
    }

    @Override
    public String[] getDependentIds() {
        return new String[] { IWatcher.class.getName() };
    }

    @Override
    public boolean isOptional(String id) throws InvalidArgumentException {
        if (IWatcher.class.getName().equals(id)) {
            return true;
        }
        throw new InvalidArgumentException("The Registry does not depends on service {}", id);
    }
}
