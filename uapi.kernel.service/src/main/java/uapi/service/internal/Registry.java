package uapi.service.internal;

import com.google.auto.service.AutoService;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.ThreadSafe;
import uapi.helper.ArgumentChecker;
import uapi.helper.Executor;
import uapi.service.IRegistry;
import uapi.service.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of IRegistry
 */
@ThreadSafe
@AutoService(IService.class)
public class Registry implements IRegistry, IService {

    private final Lock _resolvedLock;
    private final Lock _unresolvedLock;
    private final Multimap<String, ServiceHolder> _resolvedSvcs;
    private final Multimap<String, ServiceHolder> _unresolvedSvcs;

    public Registry() {
        this._resolvedLock = new ReentrantLock();
        this._unresolvedLock = new ReentrantLock();
        this._resolvedSvcs = LinkedListMultimap.create();
        this._unresolvedSvcs = LinkedListMultimap.create();
    }

    @Override
    public String[] getIds() {
        return new String[] { IRegistry.class.getCanonicalName() };
    }

    @Override
    public String[] getDependentIds() {
        return new String[0];
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
                .map(ServiceHolder::getService)
                .subscribe(svc -> resolvedSvcs.add((T) svc));
        Observable.from(this._unresolvedSvcs.values())
                .filter(svcHolder -> svcHolder.getId().equals(serviceId))
                .first()
                .subscribe(svcHolder -> {
                    throw new KernelException("Found unresolved service {}", svcHolder);
                });
        return resolvedSvcs;
    }

    private void registerService(
            final Object svc,
            final String[] svcIds) {
        Stream.of(svcIds).map(svcId -> {
            ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
            Executor.create().guardBy(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
            return svcHolder;
        }).forEach(this::newResolvedService);
    }

    private void registerService(
            final IService svc) {
        String[] svcIds = svc.getIds();
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }

        String[] dependencyIds = svc.getDependentIds();
        if (dependencyIds == null || dependencyIds.length == 0) {
            Stream.of(svcIds).map(svcId -> {
                ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
                Executor.create().guardBy(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
                return svcHolder;
            }).forEach(this::newResolvedService);
        } else {
            Stream.of(svcIds).forEach(svcId -> Executor.create().guardBy(this._unresolvedLock).run(
                    () -> this._unresolvedSvcs.put(svcId, new ServiceHolder(svc, svcId))));
        }
    }

    private void newResolvedService(final ServiceHolder resolvedService) {
        final String resolvedSvcId = resolvedService.getId();

        resolvedService.initService();

        List<ServiceHolder> dependSvcs = Executor.create().guardBy(this._unresolvedLock).runForResult(
                () -> this._unresolvedSvcs.values().stream()
                        .filter(serviceHolder -> serviceHolder.isDependsOn(resolvedSvcId)).collect(Collectors.toList()));
        Observable.from(dependSvcs)
                .doOnNext(serviceHolder -> serviceHolder.setDependency(resolvedService))
                .filter(ServiceHolder::isResolved)
                .doOnNext(serviceHolder -> {
                    Executor.create().guardBy(this._unresolvedLock).run(
                            () -> this._unresolvedSvcs.remove(serviceHolder.getId(), serviceHolder));
                    Executor.create().guardBy(this._resolvedLock).run(
                            () -> this._resolvedSvcs.put(serviceHolder.getId(), serviceHolder));
                })
                .forEach(this::newResolvedService);
    }
}
