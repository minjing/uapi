package uapi.service.internal;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.rx.Looper;
import uapi.service.*;
import uapi.state.IShifter;
import uapi.state.IStateTracer;
import uapi.state.StateCreator;
import uapi.state.internal.StateTracer;

import javax.xml.ws.FaultAction;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * The ServiceHolder hold specific service with its id and dependencies
 */
public final class ServiceHolder2 implements IServiceReference, IServiceHolder {

    private static final String OP_RESOLVE  = "resolve";
    private static final String OP_INJECT   = "inject";
    private static final String OP_SATISFY  = "satisfy";
    private static final String OP_ACTIVATE = "activate";

    private final Object _svc;
    private final String _svcId;
    private final String _from;
    private QualifiedServiceId _qualifiedSvcId;
    private final Multimap<Dependency, IServiceHolder> _dependencies;
    private final ISatisfyHook _satisfyHook;

    private final List<IServiceHolder> _injectedSvcs = new LinkedList<>();
    private final IStateTracer<ServiceState> _stateTracer;

    private final IShifter<ServiceState> _stateShifter = (currentState, operation) -> {
        if (currentState == ServiceState.Destroyed) {
            throw new KernelException("The service {} is destroyed", this._qualifiedSvcId);
        }

        ServiceState newState;
        switch(operation.type()) {
            case OP_RESOLVE:
                resolve();
                newState = ServiceState.Resolved;
                break;
            case OP_INJECT:
                inject();
                newState = ServiceState.Injected;
                break;
            case OP_SATISFY:
                satisfy();
                newState = ServiceState.Satisfied;
                break;
            case OP_ACTIVATE:
                activate();
                newState = ServiceState.Activated;
                break;
            default:
                throw new KernelException("Unsupported operation type - {}", operation.type());
        }
        return newState;
    };

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

        this._stateTracer = StateCreator.createTracer(this._stateShifter, ServiceState.Unresolved);
    }

    /********************************************
     * Methods implements for IServiceReference *
     ********************************************/
    @Override
    public String getId() {
        return this._svcId;
    }

    @Override
    public String getFrom() {
        return this._from;
    }

    @Override
    public QualifiedServiceId getQualifiedId() {
        return this._qualifiedSvcId;
    }

    @Override
    public Object getService() {
        if (tryActivate(false)) {
            return this._svc;
        }
        return null;
    }

    @Override
    public void notifySatisfied() {

    }

    /*****************************************
     * Methods implements for IServiceHolder *
     *****************************************/

    public boolean tryActivate() {
        return tryActivate(false);
    }

    public boolean tryActivate(final boolean throwException) {
        if (this._stateTracer.get().value() >= ServiceState.Activated.value()) {
            return true;
        }
        try {
            if (this._stateTracer.get().value() < ServiceState.Resolved.value()) {
                this._stateTracer.shift(OP_RESOLVE);
            }
            if (this._stateTracer.get().value() < ServiceState.Injected.value()) {
                this._stateTracer.shift(OP_INJECT);
            }
            if (this._stateTracer.get().value() < ServiceState.Satisfied.value()) {
                this._stateTracer.shift(OP_SATISFY);
            }
        } catch (Exception ex) {
            if (throwException) {
                throw ex;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isDependsOn(final String serviceId) {
        ArgumentChecker.notEmpty(serviceId, "serviceId");
        return isDependsOn(new QualifiedServiceId(serviceId, QualifiedServiceId.FROM_LOCAL));
    }

    @Override
    public boolean isDependsOn(QualifiedServiceId qualifiedServiceId) {
        ArgumentChecker.notNull(qualifiedServiceId, "qualifiedServiceId");
        return findDependencies(qualifiedServiceId) != null;
    }

    @Override
    public void setDependency(IServiceHolder service) {
        ArgumentChecker.notNull(service, "service");

        Stack<IServiceHolder> dependencyStack = new Stack<>();
        checkCycleDependency(this, dependencyStack);

        // remove null entry first
        Dependency dependency = findDependencies(service.getQualifiedId());
        if (dependency == null) {
            throw new KernelException(
                    "The service {} does not depend on service {}", this._qualifiedSvcId, service.getQualifiedId());
        }
        this._dependencies.remove(dependency, null);
        this._dependencies.put(dependency, service);
    }

    @Override
    public List<Dependency> getUnresolvedServices() {
        return Looper.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();
    }

    /*******************
     * Private methods *
     *******************/

    @Override
    public void checkCycleDependency(
            final IServiceHolder svcToCheck,
            final Stack<IServiceHolder> dependencyStack
    ) {
        dependencyStack.push(this);
        Looper.from(this._dependencies.entries())
                .filter(entry -> entry.getValue() != null)
                .map(Map.Entry::getValue)
                .next(svcHolder -> {
                    if (svcHolder == svcToCheck) {
                        dependencyStack.push(svcHolder);
                        throw new CycleDependencyException(dependencyStack);
                    }
                })
                .foreach(dependency -> dependency.checkCycleDependency(svcToCheck, dependencyStack));

        IServiceHolder svcHolder = dependencyStack.pop();
        if (svcHolder != this) {
            throw new KernelException("The last service item was not self - {}", this._qualifiedSvcId);
        }
    }

    private Dependency findDependencies(QualifiedServiceId qsId) {
        return Looper.from(this._dependencies.keySet())
                .filter(dpendQsvcId -> dpendQsvcId.getServiceId().getId().equals(qsId.getId()))
                .filter(dpendQsvcId -> dpendQsvcId.getServiceId().getFrom().equals(QualifiedServiceId.FROM_ANY) || dpendQsvcId.getServiceId().equals(qsId))
                .first(null);
    }

    @Override
    public void resolve() {
        if (this._stateTracer.get().value() >= ServiceState.Resolved.value()) {
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
                .foreach(IServiceHolder::resolve);
    }

    @Override
    public void inject() {
        if (this._stateTracer.get().value() >= ServiceState.Injected.value()) {
            return;
        }
        if (this._dependencies.size() > 0 && !(_svc instanceof IInjectable)) {
            throw new KernelException("The service {} does not implement IInjectable interface", _qualifiedSvcId);
        }

        // Inject all dependent service
        Looper.from(_dependencies.entries())
                .map(Map.Entry::getValue)
                .foreach(IServiceHolder::inject);

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
    }

    public void satisfy() {
        if (this._stateTracer.get().value() >= ServiceState.Satisfied.value()) {
            return;
        }

        Looper.from(_dependencies.entries())
                .map(Map.Entry::getValue)
                .foreach(IServiceHolder::satisfy);

        if (! _satisfyHook.isSatisfied(ServiceHolder2.this)) {
            throw new KernelException("The service {} can'be satisfied", _qualifiedSvcId);
        }
    }

    @Override
    public void activate() {
        if (this._stateTracer.get().value() >= ServiceState.Activated.value()) {
            return;
        }

        Looper.from(_dependencies.entries())
                .map(Map.Entry::getValue)
                .foreach(IServiceHolder::activate);

        if (_svc instanceof IInitial) {
            ((IInitial) _svc).init();
        }
    }
}
