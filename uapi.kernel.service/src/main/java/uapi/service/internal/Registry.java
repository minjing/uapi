package uapi.service.internal;

import com.google.auto.service.AutoService;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.ThreadSafe;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.Guarder;
import uapi.service.*;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
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
    private final Lock _unsatisfiedLock;
    private final SatisfyDecider _satisfyDecider;
    private final Multimap<String, ServiceHolder> _resolvedSvcs;
    private final Multimap<String, ServiceHolder> _unsatisfiedSvcs;
    private final List<WeakReference<IWatcher>> _watchers;
    private final List<WeakReference<ISatisfyHook>> _satisfyHooks;

    public Registry() {
        this._resolvedLock = new ReentrantLock();
        this._unsatisfiedLock = new ReentrantLock();
        this._resolvedSvcs = LinkedListMultimap.create();
        this._unsatisfiedSvcs = LinkedListMultimap.create();
        this._watchers = new CopyOnWriteArrayList<>();
        this._satisfyHooks = new CopyOnWriteArrayList<>();
        this._satisfyDecider = new SatisfyDecider();
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
        registerService(object, serviceIds, new String[0]);
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
//        Observable.from(this._resolvedSvcs.values())
//                .filter(svcHolder -> svcHolder.getId().equals(serviceId))
//                .map(ServiceHolder::getService)
//                .subscribe(svc -> resolvedSvcs.add((T) svc));
//        Observable.from(this._unsatisfiedSvcs.values())
//                .filter(svcHolder -> svcHolder.getId().equals(serviceId))
//                .first()
//                .subscribe(svcHolder -> {
//                    throw new KernelException("Found unresolved service {}", svcHolder);
//                }, t -> { /* Do nothing */ });
        Guarder.by(this._unsatisfiedLock).run(() ->
            Observable.from(this._unsatisfiedSvcs.values())
                    .filter(svcHolder -> svcHolder.getId().equals(serviceId))
                    .doOnNext(ServiceHolder::initService)
                    .map(ServiceHolder::getService)
                    .subscribe(svcHolder -> resolvedSvcs.add((T) svcHolder))
        );
        return resolvedSvcs;
    }

    int getCount() {
        return getResolvedCount() + getUnresolvedCount();
    }

    int getResolvedCount() {
        return Guarder.by(this._resolvedLock).runForResult(this._resolvedSvcs::size);
    }

    int getUnresolvedCount() {
        return Guarder.by(this._unsatisfiedLock).runForResult(this._unsatisfiedSvcs::size);
    }

    private void registerService(
            final Object svc,
            final String[] svcIds,
            final String[] dependencyIds) {
//        Observable.from(svcIds)
//                .map(svcId -> {
//                    ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
//                    Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
//                    return svcHolder;
//                })
//                .doOnNext(this::notifyRegistered)
//                .subscribe(this::newResolvedService);

        Observable.from(svcIds)
                .map(svcId -> new ServiceHolder(svc, svcId, dependencyIds, this._satisfyDecider))
                .subscribe(svcHolder -> {
                    Guarder.by(this._unsatisfiedLock).run(() -> {
                        // Check whether the new register service depends on existing service
                        Observable.from(this._unsatisfiedSvcs.values())
                                .filter(existingSvc -> CollectionHelper.isContains(dependencyIds, existingSvc.getId()))
                                .subscribe(svcHolder::setDependency);
                        // Check whether existing service depends on the new register service
                        Observable.from(this._unsatisfiedSvcs.values())
                                .filter(existingSvc -> existingSvc.isDependsOn(svcHolder.getId()))
                                .subscribe(existingSvc -> existingSvc.setDependency(svcHolder));
                        this._unsatisfiedSvcs.put(svcHolder.getId(), svcHolder);
                    });
                });
    }

    private void registerService(
            final IService svc) {
        final String[] svcIds = svc.getIds();
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }
        final String[] dependencyIds = svc instanceof IInjectable ? ((IInjectable) svc).getDependentIds() : new String[0];
        registerService(svc, svcIds, dependencyIds);

//        final String[] dependencyIds = svc instanceof IInjectable ? ((IInjectable) svc).getDependentIds() : new String[0];
//        if (dependencyIds == null || dependencyIds.length == 0) {
//            Observable.from(svcIds)
//                    .map(svcId -> {
//                        ServiceHolder svcHolder = new ServiceHolder(svc, svcId, dependencyIds);
//                        Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcId, svcHolder));
//                        return svcHolder;
//                    })
//                    .doOnNext(this::notifyRegistered)
//                    .forEach(this::newResolvedService);
//        } else {
//            Observable.from(svcIds)
//                    .map(svcId -> new ServiceHolder(svc, svcId, dependencyIds))
//                    .doOnNext(this::notifyRegistered)
//                    .forEach(svcHolder -> {
//                        if (svcHolder.isSatisfied()) {
//                            Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(svcHolder.getId(), svcHolder));
//                            newResolvedService(svcHolder);
//                        } else {
//                            Guarder.by(this._unsatisfiedLock).run(() -> this._unsatisfiedSvcs.put(svcHolder.getId(), svcHolder));
//                        }
//                    });
//        }
    }

//    private void newResolvedService(final ServiceHolder resolvedService) {
//        final String resolvedSvcId = resolvedService.getId();
//
//        resolvedService.initService();
//        notifyResolved(resolvedService);
//
//        List<ServiceHolder> dependSvcs = Guarder.by(this._unsatisfiedLock).runForResult(
//                () -> this._unsatisfiedSvcs.values().stream()
//                        .filter(serviceHolder -> serviceHolder.isDependsOn(resolvedSvcId)).collect(Collectors.toList()));
//        Observable.from(dependSvcs)
//                .doOnNext(serviceHolder -> serviceHolder.setDependency(resolvedService))
//                .filter(ServiceHolder::isSatisfied)
//                .doOnNext(serviceHolder -> {
//                    Guarder.by(this._unsatisfiedLock).run(() -> this._unsatisfiedSvcs.remove(serviceHolder.getId(), serviceHolder));
//                    Guarder.by(this._resolvedLock).run(() -> this._resolvedSvcs.put(serviceHolder.getId(), serviceHolder));
//                })
//                .forEach(this::newResolvedService);
//    }

    @Override
    public void injectObject(
            final Injection injection
    ) throws InvalidArgumentException, KernelException {
        ArgumentChecker.notNull(injection, "injection");
//        if (IWatcher.class.getName().equals(injection.getId()) && injection.getObject() instanceof IWatcher) {
//            releaseWatchers();
//            IWatcher watcher = (IWatcher) injection.getObject();
//            this._watchers.add(new WeakReference<>(watcher));
//            Observable.from(this._unsatisfiedSvcs.values())
//                    .map(svcHolder -> (IServiceReference) svcHolder)
//                    .subscribe(watcher::onRegister);
//            Observable.from(this._resolvedSvcs.values())
//                    .map(svcHolder -> (IServiceReference) svcHolder)
//                    .doOnNext(watcher::onRegister)
//                    .subscribe(watcher::onResolved);
//            return;
//        }
        if (ISatisfyHook.class.getName().equals(injection.getId()) && injection.getObject() instanceof ISatisfyHook) {
            releaseHooks();
            ISatisfyHook hook = (ISatisfyHook) injection.getObject();
            this._satisfyHooks.add(new WeakReference<>(hook));
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
        if (ISatisfyHook.class.getName().equals(id)) {
            return true;
        }
        throw new InvalidArgumentException("The Registry does not depends on service {}", id);
    }

//    private void notifyRegistered(
//            final IServiceReference svcRef
//    ) {
//        releaseWatchers();
//        Observable.from(this._watchers)
//                .filter(watcherRef -> watcherRef.get() != null)
//                .map(Reference::get)
//                .subscribe(watcher -> watcher.onRegister(svcRef));
//    }

//    private void notifyResolved(
//            final IServiceReference svcRef
//    ) {
//        releaseWatchers();
//        Observable.from(this._watchers)
//                .filter(watcherRef -> watcherRef.get() != null)
//                .map(Reference::get)
//                .subscribe(watcher -> watcher.onResolved(svcRef));
//    }

//    private void releaseWatchers() {
//        for (Iterator<WeakReference<IWatcher>> itor = this._watchers.iterator(); itor.hasNext(); ) {
//            if (itor.next().get() == null) {
//                itor.remove();
//            }
//        }
//    }

    private void releaseHooks() {
        for (Iterator<WeakReference<ISatisfyHook>> itor = this._satisfyHooks.iterator(); itor.hasNext(); ) {
            if (itor.next().get() == null) {
                itor.remove();
            }
        }
    }

    private final class SatisfyDecider implements ISatisfyHook {

        @Override
        public boolean isSatisfied(Object service) {
            boolean containsNull = false;
            boolean isSatisfied = true;
            for (WeakReference<ISatisfyHook> hookRef : Registry.this._satisfyHooks) {
                ISatisfyHook hook = hookRef.get();
                if (hook == null) {
                    containsNull = true;
                    continue;
                }
                isSatisfied = hook.isSatisfied(service);
                if (! isSatisfied) {
                    break;
                }
            }
            if (containsNull) {
                releaseHooks();
            }
            return isSatisfied;
        }
    }
}
