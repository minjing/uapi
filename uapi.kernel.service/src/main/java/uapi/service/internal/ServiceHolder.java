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
    private final Multimap<QualifiedServiceId, ServiceHolder> _dependencies;
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
        this(from, service, serviceId, new String[0], satisfyHook);
    }

    ServiceHolder(
            final String from,
            final Object service,
            final String serviceId,
            final String[] dependencies,
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
                .map(dependency -> QualifiedServiceId.splitTo(dependency, QualifiedServiceId.LOCATION))
                .subscribe(pair -> this._dependencies.put(pair, null));

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
        this._stateManagement.goon();
    }

    void start() {
        this._started = true;
        this._stateManagement.goon();
    }

    void addStateMonitor(IStateMonitor monitor) {
        this._stateMonitors.add(monitor);
        Observable.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() != null)
                .map(Map.Entry::getValue)
                .subscribe(dependency -> dependency.addStateMonitor(this._stateManagement));
        if (this._started) {
            this._stateManagement.goon();
        }
    }

    void setDependency(ServiceHolder service) {
        ArgumentChecker.notNull(service, "service");

        if (! isDependsOn(service.getQualifiedId())) {
            throw new KernelException("The service {} does not depend on service {}", this._qualifiedSvcId, service._qualifiedSvcId);
        }
        // remove null entry first
        QualifiedServiceId qsvcId = findDependentId(service.getQualifiedId());
        if (qsvcId == null) {
            throw new KernelException("The service {} does not depend on service {}", this._qualifiedSvcId, service._qualifiedSvcId);
        }
        this._dependencies.remove(qsvcId, null);
        this._dependencies.put(qsvcId, service);

        service.addStateMonitor(this._stateManagement);
        if (this._started) {
            this._stateManagement.goon();
        }
    }

    private QualifiedServiceId findDependentId(QualifiedServiceId qsId) {
        return Observable.from(this._dependencies.keySet())
                .filter(dpendQsvcId -> dpendQsvcId.getId().equals(qsId.getId()))
                .filter(dpendQsvcId -> dpendQsvcId.getFrom().equals(QualifiedServiceId.FROM_ANY) || dpendQsvcId.equals(qsId))
                .toBlocking().firstOrDefault(null);
    }

    boolean isDependsOn(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        return isDependsOn(new QualifiedServiceId(serviceId, QualifiedServiceId.FROM_LOCAL));
    }

    boolean isDependsOn(QualifiedServiceId qualifiedServiceId) {
        ArgumentChecker.notNull(qualifiedServiceId, "qualifiedServiceId");
        if (this._dependencies.containsKey(qualifiedServiceId)) {
            return true;
        }

        if (findDependentId(qualifiedServiceId) != null) {
            return true;
        }
        return false;
    }

    List<String> getUnresolvedServices(String from) {
        ArgumentChecker.notEmpty(from, "from");
        List<String> ids = Observable.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .filter(qsId -> qsId.canFrom(from))
                .map(QualifiedServiceId::getId)
                .toList().toBlocking().first();
        return ids;
    }

    boolean isInited() {
        return this._stateManagement._state == State.Initialized;
    }

    boolean isUninited() {
        return this._stateManagement._state != State.Initialized;
    }

    boolean tryInitService() {
        return this._stateManagement.goon();
    }

    @Override
    public String toString() {
        return StringHelper.makeString("Service[id={}, type={}, dependencies={}]",
                this._qualifiedSvcId, this._svc.getClass().getName(), this._dependencies);
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
                goon();
            }
        }

        private boolean goon() {
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
                    // do nothing
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
            QualifiedServiceId unsetSvc = Observable.from(ServiceHolder.this._dependencies.entries())
                    .filter(entry -> entry.getValue() == null)
                    .filter(entry -> !((IInjectable) ServiceHolder.this._svc).isOptional(entry.getKey().getId()))
                    .map(Map.Entry::getKey)
                    .toBlocking().firstOrDefault(null);
            if (unsetSvc != null) {
                return false;
            }
            // Check dependencies is all initialized
            QualifiedServiceId unSatisfiedSvc = Observable.from(ServiceHolder.this._dependencies.entries())
                    .filter(entry -> entry.getValue() != null)
                    .filter(entry -> ! entry.getValue().tryInitService())
                    .map(Map.Entry::getKey)
                    .toBlocking().firstOrDefault(null);
            if (unSatisfiedSvc != null) {
                return false;
            }

            this._state = State.Resolved;
            return tryInject();
        }

        private boolean tryInject() {
            ArgumentChecker.equals(this._state, State.Resolved, "ServiceHolder.state");

            if (ServiceHolder.this._dependencies.size() > 0) {
                if (ServiceHolder.this._svc instanceof IInjectable) {
                    Observable.from(ServiceHolder.this._dependencies.values())
                            .filter(dependency -> dependency != null)
                            .subscribe(dependency -> {
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
                            }, throwable -> {
                                throw new KernelException(throwable);
                            });
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
