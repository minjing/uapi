package uapi.kernel.internal;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

import uapi.kernel.IService;

final class ResolvedService {

    private final String                        _sid;
    private final Class<? extends IService>     _type;
    private final Map<String, Dependency>       _dependencies;
    private IService                            _instance;

    ResolvedService(Class<? extends IService> type) {
        this(type, type.getName());
    }

    ResolvedService(Class<? extends IService> type, String sid) {
        if (type == null) {
            throw new IllegalArgumentException("The argument is required - type");
        }
        if (sid == null) {
            throw new IllegalArgumentException("The argument is required - sid");
        }
        this._sid = sid;
        this._type = type;
        this._dependencies = new HashMap<>();
    }

    void addDependency(String serviceId, Class<?> serviceType, Method setter) {
        Dependency dependency = new Dependency(serviceId, serviceType, setter);
        if (this._dependencies.containsKey(dependency.getServiceId())) {
            throw new IllegalArgumentException("The dependency was exist in " + serviceType.getName() + " - " + serviceId);
        }
        this._dependencies.put(dependency.getServiceId(), dependency);
    }

    private final class Dependency {

        private final String    _sid;
        private final Class<?>  _type;
        private final Method    _setter;

        private Dependency(String sid, Class<?> type, Method setter) {
            if (type == null) {
                throw new IllegalArgumentException("The argument is required - type");
            }
            if (setter == null) {
                throw new IllegalArgumentException("The argument is required - setter");
            }
            if (sid != null) {
                this._sid = sid;
            } else {
                this._sid = type.getName();
            }
            this._type = type;
            this._setter = setter;
        }

        String getServiceId() {
            return this._sid;
        }

        Class<?> getServiceType() {
            return this._type;
        }

        Method getSetter() {
            return this._setter;
        }
    }
}
