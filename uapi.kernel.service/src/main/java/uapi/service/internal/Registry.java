/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
import uapi.helper.StringHelper;
import uapi.service.*;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Implementation of IRegistry
 */
@ThreadSafe
@AutoService(IService.class)
public class Registry implements IRegistry, IService, IInjectable {

    private final Lock _svcRepoLock;
    private final SatisfyDecider _satisfyDecider;
    private final Multimap<String, ServiceHolder> _svcRepo;
    private final List<WeakReference<ISatisfyHook>> _satisfyHooks;
    private final Map<String, IServiceLoader> _serviceLoaders;

    public Registry() {
        this._svcRepoLock = new ReentrantLock();
        this._svcRepo = LinkedListMultimap.create();
        this._satisfyHooks = new CopyOnWriteArrayList<>();
        this._satisfyDecider = new SatisfyDecider();
        this._serviceLoaders = new HashMap<>();
    }

    private volatile boolean _inited = false;

    @Override
    public void init() {
        if (this._inited) {
            return;
        }
        this._inited = true;
        Observable.from(this._svcRepo.values())
                .filter(svcHolder -> svcHolder.getService() != this)
                .subscribe(ServiceHolder::tryInitService);
    }

    @Override
    public String[] getIds() {
        return new String[] { IRegistry.class.getCanonicalName() };
    }

    @Override
    public void register(
            final IService service
    ) throws InvalidArgumentException {
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
            final Object service,
            final String... serviceIds
    ) throws InvalidArgumentException {
        register(QualifiedServiceId.FROM_LOCAL, service, serviceIds);
    }

    @Override
    public void register(
            final String serviceFrom,
            final Object service,
            final String... serviceIds
    ) throws InvalidArgumentException {
        ArgumentChecker.notEmpty(serviceFrom, "serviceFrom");
        ArgumentChecker.notNull(service, "service");
        registerService(serviceFrom, service, serviceIds, new Dependency[0]);
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
        return findServices(serviceType.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findServices(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        List<T> resolvedSvcs = new ArrayList<>();
        Guarder.by(this._svcRepoLock).run(() ->
            Observable.from(this._svcRepo.values())
                    .filter(svcHolder -> svcHolder.getId().equals(serviceId))
                    .filter(ServiceHolder::tryInitService)
                    .map(ServiceHolder::getService)
                    .subscribe(svcHolder -> resolvedSvcs.add((T) svcHolder))
        );
        return resolvedSvcs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findService(final String serviceId, final String serviceFrom) {
        List<T> found = Guarder.by(this._svcRepoLock).runForResult(() ->
                (List<T>) Observable.from(this._svcRepo.values())
                .filter(svcHolder -> svcHolder.getId().equals(serviceId))
                .filter(svcHolder -> svcHolder.getFrom().equals(serviceFrom))
                .filter(ServiceHolder::tryInitService)
                .map(ServiceHolder::getService)
                .toList().toBlocking().single()
        );
        if (found == null || found.size() == 0) {
            return null;
        }
        if (found.size() == 1) {
            return found.get(0);
        }
        throw new KernelException("Find multiple service by service id {}@{}", serviceId, serviceFrom);
    }

    public void start() {
        Observable.from(this._svcRepo.values())
                .doOnNext(ServiceHolder::start)
                .subscribe(ServiceHolder::tryInitService);
    }

    @Override
    public void registerServiceLoader(
            final IServiceLoader serviceLoader) {
        ArgumentChecker.notNull(serviceLoader, "serviceLoader");

        String name = serviceLoader.getName();
        IServiceLoader existing = this._serviceLoaders.putIfAbsent(name, serviceLoader);
        if (existing != null) {
            throw new InvalidArgumentException(
                    "There has an service loader{} named {}",
                    existing, name);
        }

        Observable.from(this._svcRepo.values())
                .filter(ServiceHolder::isUninited)
                .flatMap(serviceHolder -> Observable.from(serviceHolder.getUnresolvedServices(name)))
                .subscribe(dependency -> {
                    String svcId = dependency.getServiceId().getId();
                    Class<?> svcType = dependency.getServiceType();
                    Object svc = serviceLoader.load(svcId, svcType);
                    if (svc != null) {
                        this.register(name, svc, svcId);
                    }
                });
    }

    private void loadUnresolvedService(List<Dependency> dependencies) {
        
    }

    int getCount() {
        return Guarder.by(this._svcRepoLock).runForResult(this._svcRepo::size);
    }

    private void registerService(
            final IService svc) {
        final String[] svcIds = svc.getIds();
        final Dependency[] dependencies = svc instanceof IInjectable ? ((IInjectable) svc).getDependencies() : new Dependency[0];
        registerService(QualifiedServiceId.FROM_LOCAL, svc, svcIds, dependencies);
    }

    private void registerService(
            final String svcFrom,
            final Object svc,
            final String[] svcIds,
            final Dependency[] dependencies) {
        ArgumentChecker.notEmpty(svcFrom, "svcFrom");
        ArgumentChecker.notNull(svc, "svc");
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }

        Observable.from(svcIds)
                .map(svcId -> new ServiceHolder(svcFrom, svc, svcId, dependencies, this._satisfyDecider))
                .subscribe(svcHolder -> {
                    Guarder.by(this._svcRepoLock).run(() -> {
                        // Check whether the new register service depends on existing service
                        Observable.from(this._svcRepo.values())
                                .filter(existingSvc -> svcHolder.isDependsOn(existingSvc.getId()))
                                .subscribe(svcHolder::setDependency);
                        // Check whether existing service depends on the new register service
                        Observable.from(this._svcRepo.values())
                                .filter(existingSvc -> existingSvc.isDependsOn(svcHolder.getId()))
                                .subscribe(existingSvc -> existingSvc.setDependency(svcHolder));
                        this._svcRepo.put(svcHolder.getId(), svcHolder);
                    });
                });
    }

    @Override
    public void injectObject(
            final Injection injection
    ) throws InvalidArgumentException, KernelException {
        ArgumentChecker.notNull(injection, "injection");
        if (ISatisfyHook.class.getName().equals(injection.getId()) && injection.getObject() instanceof ISatisfyHook) {
            releaseHooks();
            ISatisfyHook hook = (ISatisfyHook) injection.getObject();
            this._satisfyHooks.add(new WeakReference<>(hook));
            return;
        }
        throw new InvalidArgumentException("The Registry does not depends on service {}", injection);
    }

    @Override
    public Dependency[] getDependencies() {
        return new Dependency[] {
                new Dependency(
                        StringHelper.makeString("{}{}{}", ISatisfyHook.class.getName(), QualifiedServiceId.LOCATION, QualifiedServiceId.FROM_LOCAL),
                        ISatisfyHook.class)
        };
    }

    @Override
    public boolean isOptional(String id) throws InvalidArgumentException {
        if (ISatisfyHook.class.getName().equals(id)) {
            return true;
        }
        throw new InvalidArgumentException("The Registry does not depends on service {}", id);
    }

    private void releaseHooks() {
        for (Iterator<WeakReference<ISatisfyHook>> itor = this._satisfyHooks.iterator(); itor.hasNext(); ) {
            if (itor.next().get() == null) {
                itor.remove();
            }
        }
    }

    private final class SatisfyDecider implements ISatisfyHook {

        @Override
        public boolean isSatisfied(IServiceReference serviceRef) {
            boolean containsNull = false;
            boolean isSatisfied = true;
            for (WeakReference<ISatisfyHook> hookRef : Registry.this._satisfyHooks) {
                ISatisfyHook hook = hookRef.get();
                if (hook == null) {
                    containsNull = true;
                    continue;
                }
                isSatisfied = hook.isSatisfied(serviceRef);
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
