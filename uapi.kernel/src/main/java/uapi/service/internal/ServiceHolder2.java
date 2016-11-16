package uapi.service.internal;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.rx.Looper;
import uapi.service.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The ServiceHolder hold specific service with its id and dependencies
 */
final class ServiceHolder2 implements IServiceReference {

    private final Object _svc;
    private final String _svcId;
    private final String _from;
    private final QualifiedServiceId _qualifiedSvcId;
    private final Multimap<Dependency, ServiceHolder2> _dependencies;
    private final ISatisfyHook _satisfyHook;
    private final StateManagement _stateManagement;

    private boolean _actived = false;

    ServiceHolder2(
            final String from,
            final Object service,
            final String serviceId,
            final ISatisfyHook satisfyHook
    ) {
        this(from, service, serviceId, new Dependency[0], satisfyHook);
    }

    ServiceHolder2(
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

        Observable.from(dependencies)
                .subscribe(dependency -> this._dependencies.put(dependency, null));

        // Create StateMonitor here since it need read dependencies information.
        this._stateManagement = new StateManagement();
    }

    /************************************************
     * Implementation Methods for IServiceReference *
     ************************************************/
    @Override
    public String getId() {
        return this._svcId;
    }

    @Override
    public QualifiedServiceId getQualifiedId() {
        return this._qualifiedSvcId;
    }

    @Override
    public Object getService() {
        return this._svc;
    }

    @Override
    public void notifySatisfied() {

    }

    void prepare(LinkedList<ServiceHolder2> stack) {
        if (this._actived) {
            return;
        }

    }

    void active() {
        this._actived = true;
    }

    private enum State {
        Unresolved(0), Resolved(10), Injected(20), Satisfied(30), Initialized(40), Destroyed(-1);

        private int _value;

        private State(int value) {
            this._value = value;
        }
    }

    private final class StateManagement {

        private volatile State _state = State.Unresolved;

        private final List<ServiceHolder2> _injectedSvcs = new LinkedList<>();

        private void checkState(State state) {
            if (this._state == state) {
                return;
            }
            if (this._state == State.Destroyed) {
                throw new KernelException("The service {} is destroyed", _qualifiedSvcId);
            }
            switch (state) {
                case Unresolved:
                    break;
                case Resolved:
                    if (this._state._value < State.Resolved._value) {
                        throw new KernelException("The service state is {}, but require {}", this._state, state);
                    }
                    break;
                case Injected:
                    if (this._state._value < State.Injected._value) {
                        throw new KernelException("The service state is {}, but require {}", this._state, state);
                    }
                    break;
                case Satisfied:
                    if (this._state._value < State.Satisfied._value) {
                        throw new KernelException("The service state is {}, but require {}", this._state, state);
                    }
                    break;
                case Initialized:
                    if (this._state._value < State.Initialized._value) {
                        throw new KernelException("The service state is {}, but require {}", this._state, state);
                    }
                    break;
                default:
                    throw new KernelException("Unsupported state enumeration - {}", state);
            }
        }

        private void resolve() {
            checkState(State.Unresolved);
            if (this._state._value >= State.Resolved._value) {
                return;
            }

            // Check dependencies is set or not
            Dependency unresolvedSvc = Looper.from(_dependencies.entries())
                    .filter(entry -> entry.getValue() == null)
                    .filter(entry -> !((IInjectable) _svc).isOptional(entry.getKey().getServiceId().getId()))
                    .map(Map.Entry::getKey)
                    .first(null);
            if (unresolvedSvc != null) {
                throw new KernelException("The dependency {} of service {} is not resolved", unresolvedSvc, _qualifiedSvcId);
            }

            Looper.from(_dependencies.entries())
                    .filter(entry -> entry.getValue() != null)
                    .map(Map.Entry::getValue)
                    .foreach(svcHolder -> svcHolder._stateManagement.resolve());

            this._state = State.Resolved;
        }

        private void inject() {
            checkState(State.Resolved);
            if (this._state._value >= State.Injected._value) {
                return;
            }
            if (_dependencies.size() > 0 && !(_svc instanceof IInjectable)) {
                throw new KernelException("The service {} does not implement IInjectable interface", _qualifiedSvcId);
            }

            // Inject all dependent service
            Looper.from(_dependencies.entries())
                    .map(Map.Entry::getValue)
                    .foreach(svcHolder -> svcHolder._stateManagement.inject());

            // Inject depended service
            Looper.from(_dependencies.values())
                    .filter(dependency -> dependency != null)
                    .foreach(dependency -> {
                        // if the service was injected before, it is not necessary to inject again
                        if (CollectionHelper.isStrictContains(this._injectedSvcs, dependency)) {
                            return;
                        }
                        Object injectedSvc = dependency.getService();
                        if (injectedSvc instanceof IServiceFactory) {
                            // Create service from service factory
                            injectedSvc = ((IServiceFactory) injectedSvc).createService(_svc);
                        }
                        ((IInjectable) _svc).injectObject(new Injection(dependency.getId(), injectedSvc));
                        this._injectedSvcs.add(dependency);
                    });

            this._state = State.Injected;
        }

        private void satisfy() {
            checkState(State.Injected);
            if (this._state._value >= State.Satisfied._value) {
                return;
            }

            Looper.from(_dependencies.entries())
                    .map(Map.Entry::getValue)
                    .foreach(svcHolder -> svcHolder._stateManagement.satisfy());

            if (! _satisfyHook.isSatisfied(ServiceHolder2.this)) {
                throw new KernelException("The service {} can'be satisfied");
            }

            this._state = State.Satisfied;
        }

        private void init() {
            checkState(State.Satisfied);
            if (this._state._value >= State.Initialized._value) {
                return;
            }

            Looper.from(_dependencies.entries())
                    .map(Map.Entry::getValue)
                    .foreach(svcHolder -> svcHolder._stateManagement.init());

            if (_svc instanceof IInitial) {
                ((IInitial) _svc).init();
            }

            this._state = State.Initialized;
        }
    }
}
