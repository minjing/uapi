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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private final Lock _unsatisfiedLock;
    private final SatisfyDecider _satisfyDecider;
    private final Multimap<String, ServiceHolder> _unsatisfiedSvcs;
    private final List<WeakReference<ISatisfyHook>> _satisfyHooks;

    public Registry() {
        this._unsatisfiedLock = new ReentrantLock();
        this._unsatisfiedSvcs = LinkedListMultimap.create();
        this._satisfyHooks = new CopyOnWriteArrayList<>();
        this._satisfyDecider = new SatisfyDecider();
    }

    private volatile boolean _inited = false;

    @Override
    public void init() {
        if (this._inited) {
            return;
        }
        this._inited = true;
        Observable.from(this._unsatisfiedSvcs.values())
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
        register(FROM_LOCAL, service, serviceIds);
    }

    @Override
    public void register(
            final String serviceFrom,
            final Object service,
            final String... serviceIds
    ) throws InvalidArgumentException {
        ArgumentChecker.notEmpty(serviceFrom, "serviceFrom");
        ArgumentChecker.notNull(service, "service");
        registerService(serviceFrom, service, serviceIds, new String[0]);
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
        Guarder.by(this._unsatisfiedLock).run(() ->
            Observable.from(this._unsatisfiedSvcs.values())
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
        List<T> found = Guarder.by(this._unsatisfiedLock).runForResult(() ->
                (List<T>) Observable.from(this._unsatisfiedSvcs.values())
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

    int getCount() {
        return Guarder.by(this._unsatisfiedLock).runForResult(this._unsatisfiedSvcs::size);
    }

    private void registerService(
            final IService svc) {
        final String[] svcIds = svc.getIds();
        final String[] dependencyIds = svc instanceof IInjectable ? ((IInjectable) svc).getDependentIds() : new String[0];
        registerService(FROM_LOCAL, svc, svcIds, dependencyIds);
    }

    private void registerService(
            final String svcFrom,
            final Object svc,
            final String[] svcIds,
            final String[] dependencyIds) {
        ArgumentChecker.notEmpty(svcFrom, "svcFrom");
        ArgumentChecker.notNull(svc, "svc");
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }

        Observable.from(svcIds)
                .map(svcId -> new ServiceHolder(svcFrom, svc, svcId, dependencyIds, this._satisfyDecider))
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
    public String[] getDependentIds() {
        return new String[] { ISatisfyHook.class.getName() };
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
