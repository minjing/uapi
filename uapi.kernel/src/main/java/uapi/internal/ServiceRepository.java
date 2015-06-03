package uapi.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.helper.ClassHelper;
import uapi.service.IService;
import uapi.service.IServiceRepository;
import uapi.service.Inject;
import uapi.service.Registration;
import uapi.service.Type;

public class ServiceRepository implements IService, IServiceRepository {

    private final Map<String, List<StatefulService>>        _uninitializedServices;
    private final Map<String, List<StatefulService>>        _initializedServices;
    private final ServiceExtractor                          _serviceExtractor;

    @Inject
    private final Map<Class<?>, List<IAnnotationParser<?>>> _annotationParsers;

    public ServiceRepository() {
        this._uninitializedServices = new HashMap<>();
        this._initializedServices   = new HashMap<>();
        this._serviceExtractor      = new ServiceExtractor();
        this._annotationParsers     = new HashMap<>();
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
                addService(service, type.value().toString());
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
            storeService(svc, this._initializedServices);
        } else {
            storeService(svc, this._uninitializedServices);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return (T) getService(serviceType.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        Object[] svcs = getServices(serviceId);
        if (svcs.length == 0) {
            return null;
        } else if (svcs.length == 1) {
            return (T) svcs[0];
        } else {
            throw new KernelException("Found more than one service associate with {}", serviceId);
        }
    }

    public Object[] getServices(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getServices(serviceType.getName());
    }

    public Object[] getServices(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        Collection<Object> svcInsts;
        List<StatefulService> svcs = this._initializedServices.get(serviceId);
        if (svcs != null) {
            svcInsts = Collections2.transform(svcs, this._serviceExtractor);
        } else {
            svcInsts = new ArrayList<>();
        }
        svcs = this._uninitializedServices.remove(serviceId);
        if (svcs != null) {
            for (int i = 0; i < svcs.size(); i++) {
                StatefulService svc = svcs.get(i);
                svcInsts.add(svc.getInstance());
                storeService(svc, this._initializedServices);
            }
        }
        return svcInsts.toArray();
    }

    private void storeService(StatefulService service, Map<String, List<StatefulService>> serviceMap) {
        List<StatefulService> svcs = serviceMap.get(service.getName());
        if (svcs == null) {
            svcs = new ArrayList<>();
            serviceMap.put(service.getName(), svcs);
        }
        svcs.add(service);
    }

    @Override
    public void addAnnotationParser(IAnnotationParser<?> parser) {
        if (parser == null) {
            throw new InvalidArgumentException("parser", InvalidArgumentType.EMPTY);
        }
        Class<?>[] annotationTypes = ClassHelper.getInterfaceParameterizedClasses(parser.getClass(), IAnnotationParser.class);
        if (annotationTypes == null) {
            throw new KernelException("The parser {} does not specified parameter type", parser.getClass().getName());
        }
        Class<?> annotationType = annotationTypes[0];
        List<IAnnotationParser<?>> parsers = this._annotationParsers.get(annotationType);
        if (parsers == null) {
            parsers = new ArrayList<>();
            this._annotationParsers.put(annotationType, parsers);
        }
        parsers.add(parser);
    }

    private static final class ServiceExtractor implements Function<StatefulService, Object> {

        @Override
        public Object apply(StatefulService input) {
            return input.getInstance();
        }
    }
}
