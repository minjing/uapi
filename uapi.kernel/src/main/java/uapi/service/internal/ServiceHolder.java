/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.*;

import java.util.*;

/**
 * The ServiceHolder hold specific service with its id and dependencies
 */
class ServiceHolder implements IServiceReference {

    private final Object _svc;
    private final String _svcId;
    private final String _from;
    private final QualifiedServiceId _qualifiedSvcId;
    private final Multimap<Dependency, ServiceHolder> _dependencies;
    private final ISatisfyHook _satisfyHook;
    private final List<IStateMonitor> _stateMonitors;
    private final StateManagement _stateManagement;

    private boolean _started = false;

    ServiceHolder(
            final String from,
            final Object service,
            final String serviceId,
            final ISatisfyHook satisfyHook
    ) {
        this(from, service, serviceId, new Dependency[0], satisfyHook);
    }

    ServiceHolder(
            final String from,
            final Object service,
            final String serviceId,
            final Dependency[] dependencies,
            final ISatisfyHook satisfyHook
    ) {
        ArgumentChecker.notNull(from, "from");
        ArgumentChecker.notNull(service, "service");
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        ArgumentChecker.notNull(dependencies, "dependencies");
        ArgumentChecker.notNull(satisfyHook, "satisfyHook");
        this._svc = service;
        this._svcId = serviceId;
        this._from = from;
        this._qualifiedSvcId = new QualifiedServiceId(serviceId, from);
        this._satisfyHook = satisfyHook;
        this._dependencies = LinkedListMultimap.create();
        this._stateMonitors = new LinkedList<>();

        Observable.from(dependencies)
                .subscribe(dependency -> this._dependencies.put(dependency, null));

        // Create StateMonitor here since it need read dependencies information.
        this._stateManagement = new StateManagement();
    }

    @Override
    public String getId() {
        return this._svcId;
    }

    @Override
    public QualifiedServiceId getQualifiedId() {
        return this._qualifiedSvcId;
    }

    public String getFrom() {
        return this._from;
    }

    @Override
    public Object getService() {
        return this._svc;
    }

    @Override
    public void notifySatisfied() {
        this._stateManagement.goon(null);
    }

    void start() {
        this._started = true;
        this._stateManagement.goon(null);
    }

    void addStateMonitor(IStateMonitor monitor) {
        this._stateMonitors.add(monitor);
        Observable.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() != null)
                .map(Map.Entry::getValue)
                .filter(dependency -> ! dependency.isMonitored(this._stateManagement))
                .subscribe(dependency -> dependency.addStateMonitor(this._stateManagement));
        if (this._started) {
            this._stateManagement.goon(null);
        }
    }

    boolean isMonitored(IStateMonitor monitor) {
        return this._stateMonitors.contains(monitor);
    }

    void setDependency(ServiceHolder service) {
        ArgumentChecker.notNull(service, "service");

        if (! isDependsOn(service.getQualifiedId())) {
            throw new KernelException("The service {} does not depend on service {}", this._qualifiedSvcId, service._qualifiedSvcId);
        }
        // remove null entry first
        Dependency dependency = findDependencies(service.getQualifiedId());
        if (dependency == null) {
            throw new KernelException("The service {} does not depend on service {}", this._qualifiedSvcId, service._qualifiedSvcId);
        }
        this._dependencies.remove(dependency, null);
        this._dependencies.put(dependency, service);

        service.addStateMonitor(this._stateManagement);
        if (this._started) {
            this._stateManagement.goon(service.getQualifiedId());
        }
    }

    private Dependency findDependencies(QualifiedServiceId qsId) {
        return Observable.from(this._dependencies.keySet())
                .filter(dpendQsvcId -> dpendQsvcId.getServiceId().getId().equals(qsId.getId()))
                .filter(dpendQsvcId -> dpendQsvcId.getServiceId().getFrom().equals(QualifiedServiceId.FROM_ANY) || dpendQsvcId.getServiceId().equals(qsId))
                .toBlocking().firstOrDefault(null);
    }

    boolean isDependsOn(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        return isDependsOn(new QualifiedServiceId(serviceId, QualifiedServiceId.FROM_LOCAL));
    }

    boolean isDependsOn(QualifiedServiceId qualifiedServiceId) {
        ArgumentChecker.notNull(qualifiedServiceId, "qualifiedServiceId");
//        if (this._dependencies.containsKey(qualifiedServiceId)) {
//            return true;
//        }

        if (findDependencies(qualifiedServiceId) != null) {
            return true;
        }
        return false;
    }

    List<Dependency> getUnresolvedServices(String from) {
        ArgumentChecker.notEmpty(from, "from");
        return Observable.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
//                .map(Dependency::getServiceId)
                .filter(dependency -> dependency.getServiceId().canFrom(from))
//                .map(QualifiedServiceId::getId)
                .toList().toBlocking().first();
    }

    List<Dependency> getUnresolvedServices() {
        return Looper.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();
    }

    boolean isInited() {
        return this._stateManagement._state == State.Initialized;
    }

    boolean isUninited() {
        return this._stateManagement._state != State.Initialized;
    }

    boolean tryInitService() {
        return this._stateManagement.goon(null);
    }

    @Override
    public String toString() {
        return StringHelper.makeString("Service[id={}, type={}]",
                this._qualifiedSvcId, this._svc.getClass().getName());
    }

    private enum State {
        Unresolved, Resolved, Injected, Satisfied, Initialized
    }

    private interface IStateMonitor {

        void onInitialized(QualifiedServiceId qsId);
    }

    private final class StateManagement implements IStateMonitor {

        private volatile State _state = State.Unresolved;

        private volatile boolean _changing = false;

        private final Map<QualifiedServiceId, Boolean> _dependencyStatus = new HashMap<>();
        private final List<ServiceHolder> _injectedSvcs = new LinkedList<>();

        private StateManagement() {
            Observable.from(ServiceHolder.this._dependencies.keySet())
                    .map(Dependency::getServiceId)
                    .subscribe(qsId -> this._dependencyStatus.put(qsId, false));
//            goon();
        }

        public void onInitialized(final QualifiedServiceId qsId) {
            if (this._dependencyStatus.put(qsId, true) == null) {
                if (this._dependencyStatus.remove(new QualifiedServiceId(qsId.getId(), QualifiedServiceId.FROM_ANY)) == null) {
                    throw new InvalidArgumentException("The service {} does not depends on {}",
                            ServiceHolder.this._qualifiedSvcId, qsId);
                }
            }
            boolean allSatified = Observable.from(this._dependencyStatus.values())
                    .filter(satisfied -> ! satisfied)
                    .toBlocking().firstOrDefault(true);
            if (allSatified) {
                goon(qsId);
            }
        }

        private boolean goon(QualifiedServiceId qSvcId) {
            if (this._changing) {
                return this._state == State.Initialized;
            }
            this._changing = true;
            boolean successful = false;
            switch (this._state) {
                case Unresolved:
                    successful = tryResolve();
                    break;
                case Resolved:
                    successful = tryInject();
                    break;
                case Injected:
                    successful = trySatisfy();
                    break;
                case Satisfied:
                    successful = tryInit();
                    break;
                case Initialized:
                    if (qSvcId != null) {
                        injectDependency(qSvcId);
                    }
                    successful = true;
                    break;
                default:
                    throw new KernelException("Unsupported state {}", this._state);
            }
            if (! successful) {
                this._changing = false;
                return false;
            }

            // Notify upstream services
            Observable.from(ServiceHolder.this._stateMonitors)
                    .subscribe(monitor -> monitor.onInitialized(ServiceHolder.this._qualifiedSvcId));
            ServiceHolder.this._stateMonitors.clear();

            this._changing = false;
            return this._state == State.Initialized;
        }

        private boolean tryResolve() {
            ArgumentChecker.equals(this._state, State.Unresolved, "ServiceHolder.state");

            // Check dependencies is set or not
            Dependency unsetSvc = Observable.from(ServiceHolder.this._dependencies.entries())
                    .filter(entry -> entry.getValue() == null)
                    .filter(entry -> !((IInjectable) ServiceHolder.this._svc).isOptional(entry.getKey().getServiceId().getId()))
                    .map(Map.Entry::getKey)
                    .toBlocking().firstOrDefault(null);
            if (unsetSvc != null) {
                return false;
            }
            // Check dependencies is all initialized
            Dependency unSatisfiedSvc = Observable.from(ServiceHolder.this._dependencies.entries())
                    .filter(entry -> entry.getValue() != null)
                    .filter(entry -> ! entry.getValue().tryInitService() && !((IInjectable) ServiceHolder.this._svc).isOptional(entry.getKey().getServiceId().getId()))
                    .map(Map.Entry::getKey)
                    .toBlocking().firstOrDefault(null);
            if (unSatisfiedSvc != null) {
                return false;
            }

            this._state = State.Resolved;
            return tryInject();
        }

        private void injectDependency(QualifiedServiceId qSvcId) {
            ArgumentChecker.equals(this._state, State.Initialized, "ServiceHolder.state");

            ServiceHolder svcHolder = Looper.from(ServiceHolder.this._dependencies.entries())
                    .filter(entry -> entry.getKey().getServiceId().equals(qSvcId))
                    .filter(entry -> entry.getValue().isInited())
                    .map(entry -> entry.getValue())
                    .first(null);
            if (svcHolder == null) {
                return;
            }
            Object svc = svcHolder.getService();
            if (svc == null) {
                throw new KernelException("The dependency service {} is not created", qSvcId);
            }
            if (svc instanceof IServiceFactory) {
                // Create service from service factory
                svc = ((IServiceFactory) svc).createService(ServiceHolder.this._svc);
            }
            ((IInjectable) ServiceHolder.this._svc).injectObject(new Injection(qSvcId.getId(), svc));
            if (ServiceHolder.this._svc instanceof IServiceListener) {
                ((IServiceListener) ServiceHolder.this._svc).onDependencySet(qSvcId.getId(), svc);
            }
            this._injectedSvcs.add(svcHolder);
        }

        private boolean tryInject() {
            ArgumentChecker.equals(this._state, State.Resolved, "ServiceHolder.state");

            if (ServiceHolder.this._dependencies.size() > 0) {
                if (ServiceHolder.this._svc instanceof IInjectable) {
                    Looper.from(ServiceHolder.this._dependencies.values())
                            .filter(dependency -> dependency != null)
                            .filter(ServiceHolder::isInited)
                            .foreach(dependency -> {
                                // if the service was injected before, it is not necessary to inject again
                                if (CollectionHelper.isStrictContains(this._injectedSvcs, dependency)) {
                                    return;
                                }
                                Object injectedSvc = dependency.getService();
                                if (injectedSvc instanceof IServiceFactory) {
                                    // Create service from service factory
                                    injectedSvc = ((IServiceFactory) injectedSvc).createService(ServiceHolder.this._svc);
                                }
                                ((IInjectable) ServiceHolder.this._svc).injectObject(new Injection(dependency.getId(), injectedSvc));
                                this._injectedSvcs.add(dependency);
                            });
//                    Observable.from(ServiceHolder.this._dependencies.values())
//                            .filter(ServiceHolder::isInited)
//                            .filter(dependency -> dependency != null)
//                            .subscribe(dependency -> {
//                                // if the service was injected before, it is not necessary to inject again
//                                if (CollectionHelper.isStrictContains(this._injectedSvcs, dependency)) {
//                                    return;
//                                }
//                                Object injectedSvc = dependency.getService();
//                                if (injectedSvc instanceof IServiceFactory) {
//                                    // Create service from service factory
//                                    injectedSvc = ((IServiceFactory) injectedSvc).createService(ServiceHolder.this._svc);
//                                }
//                                ((IInjectable) ServiceHolder.this._svc).injectObject(new Injection(dependency.getId(), injectedSvc));
//                                this._injectedSvcs.add(dependency);
//                            }, throwable -> {
//                                throw new KernelException(throwable);
//                            });
                } else {
                    throw new KernelException("The service {} does not implement IInjectable interface so it can't inject any dependencies");
                }
            }

            this._state = State.Injected;
            this._injectedSvcs.clear();
            return trySatisfy();
        }

        private boolean trySatisfy() {
            ArgumentChecker.equals(this._state, State.Injected, "ServiceHolder.state");

            if (! ServiceHolder.this._satisfyHook.isSatisfied(ServiceHolder.this)) {
                return false;
            }

            this._state = State.Satisfied;
            return tryInit();
        }

        private boolean tryInit() {
            ArgumentChecker.equals(this._state, State.Satisfied, "ServiceHolder.state");

            if (ServiceHolder.this._svc instanceof IInitial) {
                ((IInitial) ServiceHolder.this._svc).init();
            }

            this._state = State.Initialized;
            return true;
        }
    }
}
