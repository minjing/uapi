package uapi.service.internal;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import rx.Observable;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.helper.StringHelper;
import uapi.service.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * The ServiceHolder hold specific service with its id and dependencies
 */
class ServiceHolder implements IServiceReference {

    private final Object _svc;
    private final String _svcId;
    private final String _from;
    private final QualifiedServiceId _qualifiedSvcId;
    private final Multimap<QualifiedServiceId, ServiceHolder> _dependencies;
    private volatile boolean _inited = false;
    private volatile boolean _lazyInit = true;
    private final ISatisfyHook _satisfyHook;
    private final List<StateMonitor> _stateMonitors;
    private final StateMonitor _stateMonitor;

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
        if (service instanceof IInitial) {
            this._lazyInit = ((IInitial) service).lazy();
        }
        Observable.from(dependencies)
                .map(dependency -> QualifiedServiceId.splitTo(dependency, IRegistry.LOCATION))
                .subscribe(pair -> this._dependencies.put(pair, null));
//        Stream.of(dependencies).forEach(dependency -> this._dependencies.put(dependency, null));
        // Create StateMonitor here since it need read dependencies information.
        this._stateMonitor = new StateMonitor();
    }

    @Override
    public String getId() {
        return this._svcId;
    }

    public String getFrom() {
        return this._from;
    }

    @Override
    public Object getService() {
        return this._svc;
    }

    QualifiedServiceId getQualifiedServiceId() {
        return this._qualifiedSvcId;
    }

    void addStateMonitor(StateMonitor monitor) {
        this._stateMonitors.add(monitor);
        // If some service register this service stats which mean
        // that service is not lazy, so this service have to init
        // its self if possible
        if (this._lazyInit) {
            this._lazyInit = false;
            Observable.from(this._dependencies.entries())
                    .filter(entry -> entry.getValue() != null)
                    .map(Map.Entry::getValue)
                    .subscribe(dependency -> dependency.addStateMonitor(this._stateMonitor));
        }
    }

    void setDependency(ServiceHolder service) {
        ArgumentChecker.notNull(service, "service");
        if (! this._dependencies.containsKey(service.getId())) {
            throw new KernelException("The service {} does not depend on service {}", this._svcId, service._svcId);
        }
        // remove null entry first
        QualifiedServiceId qsvcId = service.getQualifiedServiceId();
        this._dependencies.remove(qsvcId, null);
        this._dependencies.put(qsvcId, service);
        if (! this._lazyInit) {
            service.addStateMonitor(this._stateMonitor);

        }
    }

    boolean isDependsOn(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        return this._dependencies.containsKey(serviceId);
    }

    boolean isDependsOn(QualifiedServiceId qualifiedServiceId) {
        ArgumentChecker.notNull(qualifiedServiceId, "qualifiedServiceId");
        if (this._dependencies.containsKey(qualifiedServiceId)) {
            return true;
        }
        List<QualifiedServiceId> matched = Observable.from(this._dependencies.keySet())
                .filter(dpendQsvcId -> dpendQsvcId.getId().equals(qualifiedServiceId.getId()))
                .filter(dpendQsvcId -> dpendQsvcId.getFrom().equals(IRegistry.FROM_ANY))
                .toList().toBlocking().single();
        if (matched != null && matched.size() > 0) {
            return true;
        }
        return false;
    }

    boolean isInited() {
        return this._inited;
    }

    boolean isSatisfied() {
        // Filter out dependencies which are not optional but was not set by now
        Optional<Map.Entry<QualifiedServiceId, ServiceHolder>> unresolvedSvc =
                this._dependencies.entries().stream()
                        .filter(entry -> entry.getValue() == null)
                        .filter(entry -> ! ((IInjectable) this._svc).isOptional(entry.getKey().getId()))
                        .findFirst();
        if (unresolvedSvc.isPresent()) {
            return false;
        }
        // Find out dependencies which are not satisfied
        Optional<Map.Entry<QualifiedServiceId, ServiceHolder>> unsatisfiedSvc =
                this._dependencies.entries().stream()
                        .filter(entry -> entry.getValue() != null)
                        .filter(entry -> ! entry.getValue().isSatisfied())
                        .findFirst();
        if (unsatisfiedSvc.isPresent()) {
            return false;
        }
        return this._satisfyHook.isSatisfied(this._svc);
    }

    private boolean isResolved() {
        // Filter out dependencies which are not optional but was not set by now
        Optional<Map.Entry<QualifiedServiceId, ServiceHolder>> unresolvedSvc =
                this._dependencies.entries().stream()
                        .filter(entry -> entry.getValue() == null)
                        .filter(entry -> ! ((IInjectable) this._svc).isOptional(entry.getKey().getId()))
                        .findFirst();
        if (unresolvedSvc.isPresent()) {
            return false;
        }
        return true;
    }

    private boolean isDependenciesSatisfied() {
        // Find out dependencies which are not satisfied
        Optional<Map.Entry<QualifiedServiceId, ServiceHolder>> unsatisfiedSvc =
                this._dependencies.entries().stream()
                        .filter(entry -> entry.getValue() != null)
                        .filter(entry -> ! entry.getValue().isSatisfied())
                        .findFirst();
        if (unsatisfiedSvc.isPresent()) {
            return false;
        }
        return true;
    }

    private boolean tryInjectDependencies() {
        if (! isResolved()) {
            return false;
        }
        if (! isDependenciesSatisfied()) {
            return false;
        }
        if (this._dependencies.size() > 0) {
            if (this._svc instanceof IInjectable) {
                Observable.from(this._dependencies.values())
                        .filter(dependency -> dependency != null)
                        .doOnNext(ServiceHolder::tryInitService)
                        .subscribe(dependency -> {
                            Object injectedSvc = dependency.getService();
                            if (injectedSvc instanceof IServiceFactory) {
                                // Create service from service factory
                                injectedSvc = ((IServiceFactory) injectedSvc).createService(this._svc);
                            }
                            ((IInjectable) this._svc).injectObject(new Injection(dependency.getId(), injectedSvc));
                        }, throwable -> { throw new KernelException(throwable); });
            } else {
                throw new KernelException("The service {} does not implement IInjectable interface so it can't inject any dependencies");
            }
        }
        return true;
    }

    boolean tryInitService() {
        if (this._inited) {
            return true;
        }
        if (! tryInjectDependencies()) {
            return false;
        }
        if (! this._satisfyHook.isSatisfied(this._svc)) {
            return false;
        }
        if (this._svc instanceof IInitial) {
            ((IInitial) this._svc).init();
        }
        this._inited = true;
        return true;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("Service[id={}, type={}, dependencies={}]",
                this._svcId, this._svc.getClass().getName(), this._dependencies);
    }

    private enum State {
        Unresolved, Resolved, Injected, Satisfied, Initialized
    }

    private final class StateMonitor {

        private volatile State _state = State.Unresolved;

        private final Map<QualifiedServiceId, Boolean> _dependencyStatus = new HashMap<>();

        private StateMonitor() {
            Observable.from(ServiceHolder.this._dependencies.keySet())
                    .subscribe(qsId -> this._dependencyStatus.put(qsId, false));
            goon();
        }

        private void onSatisfied(final QualifiedServiceId qsId) {
            if (this._dependencyStatus.put(qsId, true) == null) {
                throw new InvalidArgumentException("The service {} does not depends on {}",
                        ServiceHolder.this._qualifiedSvcId, qsId);
            }
            boolean allSatified = Observable.from(this._dependencyStatus.values())
                    .filter(satisfied -> ! satisfied)
                    .toBlocking().firstOrDefault(true);
            if (allSatified) {
                goon();
            }
        }

        private void goon() {
            switch (this._state) {
                case Unresolved:
                    tryResolve();
                    break;
                case Resolved:
                    tryInject();
                    break;
                case Injected:
                    trySatisfy();
                    break;
                case Satisfied:
                    tryInit();
                    break;
                case Initialized:
                    // do nothing
                    break;
                default:
                    throw new KernelException("Unsupported state {}", this._state);
            }
        }

        private void tryResolve() {
            ArgumentChecker.equals(this._state, State.Unresolved, "ServiceHolder.state");

            this._state = State.Resolved;
            tryInject();
        }

        private void tryInject() {
            ArgumentChecker.equals(this._state, State.Resolved, "ServiceHolder.state");

            this._state = State.Injected;
            trySatisfy();
        }

        private void trySatisfy() {
            ArgumentChecker.equals(this._state, State.Injected, "ServiceHolder.state");

            this._state = State.Satisfied;
            tryInit();
        }

        private void tryInit() {
            ArgumentChecker.equals(this._state, State.Satisfied, "ServiceHolder.state");

            this._state = State.Initialized;
        }
    }
}
