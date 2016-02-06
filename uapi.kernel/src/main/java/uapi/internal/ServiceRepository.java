package uapi.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.KernelException;
import uapi.helper.ClassHelper;
import uapi.helper.Executor;
import uapi.service.IAnnotationMethodHandler;
import uapi.service.IService;
import uapi.service.Inject;
import uapi.service.OnInit;
import uapi.service.Registration;
import uapi.service.Type;

public class ServiceRepository implements IService {

    private final Lock _uninitedSvcsLock;
    private final Lock _initedSvcsLock;

    private final Multimap<String, StatefulService> _uninitedSvcs;
    private final Multimap<String, StatefulService> _initedSvcs;
    private final Multimap<String, StatefulService> _forceInitSvcs;

    @Inject
    private final Map<Class<?>, List<IAnnotationMethodHandler<?>>> _annotationHandlers;

    public ServiceRepository() {
        this._uninitedSvcsLock      = new ReentrantLock();
        this._initedSvcsLock        = new ReentrantLock();
        this._uninitedSvcs          = LinkedListMultimap.create();
        this._initedSvcs            = LinkedListMultimap.create();
        this._forceInitSvcs         = LinkedListMultimap.create();
        this._annotationHandlers    = new HashMap<>();
    }

    public void addServices(List<IService> services) {
        for (IService service : services) {
            addService(service);
        }
    }

    public void addService(Object service) {
        if (service == null) {
            throw new InvalidArgumentException("service", InvalidArgumentType.EMPTY);
        }
        Registration reg = service.getClass().getAnnotation(Registration.class);
        if (reg == null) {
            addService(service, service.getClass().getName());
        } else {
            for (String name : reg.names()) {
                addService(service, name);
            }
            for (Type type : reg.value()) {
                addService(service, type.value().getName());
            }
        }
    }

    /**
     * Add outside service into the repository
     * 
     * @param sid
     * @param service
     */
    public void addService(Object service, String sid) {
        if (service == null) {
            throw new InvalidArgumentException("service", InvalidArgumentType.EMPTY);
        }
        if (Strings.isNullOrEmpty(sid)) {
            throw new InvalidArgumentException("sid", InvalidArgumentType.EMPTY);
        }
        StatefulService svc = new StatefulService(this, service, sid);
        if (svc.isInitialized()) {
            Executor.create().guardBy(this._initedSvcsLock).run(() -> {
                this._initedSvcs.put(svc.getName(), svc);
            });
        } else if (! svc.isLazyInit()) {
            this._forceInitSvcs.put(svc.getName(), svc);
        } else {
            Executor.create().guardBy(this._uninitedSvcsLock).run(() -> {
                this._uninitedSvcs.put(svc.getName(), svc);
            });
        }
    }

    public <T> T getService(Class<T> serviceType) {
        return getService(serviceType, null);
    }

    public <T> T getService(Class<T> serviceType, Object serveFor) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getService(serviceType.getName(), serveFor);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(String serviceId, Object serveFor) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        Object[] svcs = getServices(serviceId, serveFor);
        if (svcs.length == 0) {
            return null;
        } else if (svcs.length == 1) {
            return (T) svcs[0];
        } else {
            throw new KernelException("Found more than one service associate with {}", serviceId);
        }
    }

    public <T> T[] getServices(Class<T> serviceType, Object serveFor) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getServices(serviceType.getName(), serveFor);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getServices(String serviceId, Object serveFor) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        final List<Object> svcInsts = new ArrayList<>();
        // Find service from initialized map
        Collection<StatefulService> svcs = Executor.create().guardBy(this._initedSvcsLock).getResult(() -> {
            return this._initedSvcs.get(serviceId);
        });
        if (svcs != null) {
            svcs.stream()
                .map((svc) -> { return svc.getInstance(serveFor); })
                .forEach((svcInst) -> { svcInsts.add(svcInst); });
        }
        // Find service from un-initialized map
        svcs = Executor.create().guardBy(this._uninitedSvcsLock).getResult(() -> {
            return this._uninitedSvcs.removeAll(serviceId);
        });
        if (svcs != null) {
            svcs.parallelStream().forEach((svc) -> {
                svcInsts.add(svc.getInstance(serveFor));
                Executor.create().guardBy(this._initedSvcsLock).run(() -> {
                    this._initedSvcs.put(svc.getName(), svc);
                });
            });
        }
        // Find service from force-initialized map
        svcs = this._forceInitSvcs.removeAll(serviceId);
        if (svcs != null) {
            svcs.parallelStream().forEach((svc) -> {
                svcInsts.add(svc.getInstance(serveFor));
            });
        }
        return ((T[]) svcInsts.toArray());
    }

    public void addAnnotationHandler(IAnnotationMethodHandler<?> handler) {
        if (handler == null) {
            throw new InvalidArgumentException("parser", InvalidArgumentType.EMPTY);
        }
        Class<?>[] annotationTypes = ClassHelper.getInterfaceParameterizedClasses(handler.getClass(), IAnnotationMethodHandler.class);
        if (annotationTypes == null) {
            throw new KernelException("The parser {} does not specified parameter type", handler.getClass().getName());
        }
        Class<?> annotationType = annotationTypes[0];
        List<IAnnotationMethodHandler<?>> handlers = this._annotationHandlers.get(annotationType);
        if (handlers == null) {
            handlers = new ArrayList<>();
            this._annotationHandlers.put(annotationType, handlers);
        }
        handlers.add(handler);
    }

    List<IAnnotationMethodHandler<?>> getAnnotationHandlers(Class<?> annoType) {
        return this._annotationHandlers.get(annoType);
    }

    @OnInit
    public void init() {
        this._forceInitSvcs.asMap().forEach((name, svcs) -> {
            svcs.forEach((svc) -> {
                if (svc.getInstance(null) == null) {
                    throw new KernelException("Initialize service {} failed.", name);
                }
                this._initedSvcs.put(name, svc);
            });
        });
        this._forceInitSvcs.clear();
    }
}
