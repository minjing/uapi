package uapi.kernel.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

import com.google.common.base.Strings;

import uapi.kernel.Attribute;
import uapi.kernel.Init;
import uapi.kernel.Inject;
import uapi.kernel.InvalidArgumentException;
import uapi.kernel.InvalidStateException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.helper.ClassHelper;
import uapi.kernel.helper.StringHelper;
import uapi.kernel.KernelException;

final class StatefulService {

    private final ServiceRepository         _serviceRepo;
    private final Class<?>                  _type;
    private final Object                   _instance;
    private final Map<String, Dependency>   _dependencies;
    private final Lifecycle                 _lifecycle;

    private String      _sid;
    private boolean     _initAtLaunching;
    private Method      _initMethod;

    StatefulService(ServiceRepository serviceRepository, Object instance) {
        this(serviceRepository, instance, null);
    }

    StatefulService(ServiceRepository serviceRepository, Object instance, String sid) {
        if (serviceRepository == null) {
            throw new InvalidArgumentException("serviceRepository", InvalidArgumentType.EMPTY);
        }
        if (instance == null) {
            throw new InvalidArgumentException("type", InvalidArgumentType.EMPTY);
        }

        if (! Strings.isNullOrEmpty(sid)) {
            this._sid = sid;
        }
        this._serviceRepo = serviceRepository;
        this._type = instance.getClass();
        this._instance = instance;
        this._dependencies = new HashMap<>();
        this._lifecycle = new Lifecycle();
        this._lifecycle.resolve();
    }

    String getId() {
        return this._sid;
    }

    Class<?> getType() {
        return this._type;
    }

    boolean isInitialized() {
        return this._lifecycle.isInitialized();
    }

    boolean initAtLaunching() {
        return this._initAtLaunching;
    }

    boolean hasInitMethod() {
    	return this._initMethod != null;
    }

    @SuppressWarnings("unchecked")
    <T> T getInstance() {
        this._lifecycle.satisfy();
        this._lifecycle.initialize();
        return (T) this._instance;
    }

    final class Dependency {

        private final String    _sid;
        private final Class<?>  _type;
        private final Method    _setter;

        Dependency(String sid, Class<?> type, Method setter) {
            if (type == null) {
                throw new InvalidArgumentException("type", InvalidArgumentType.EMPTY);
            }
            if (setter == null) {
                throw new InvalidArgumentException("setter", InvalidArgumentType.EMPTY);
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

        void setInstance(Object instance) {
            if (instance == null) {
                throw new InvalidArgumentException("instance", InvalidArgumentType.EMPTY);
            }
            try {
                this._setter.invoke(StatefulService.this._instance, instance);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new KernelException(e, "Set dependency {} failed - {}", this._setter.getName(), StatefulService.this._sid);
            }
            StatefulService.this._dependencies.remove(this._sid);
        }
    }

    final class Lifecycle {

        private volatile ServiceState   _state;

        private Lifecycle() {
            this._state = ServiceState.UNDEFINED;
        }

        boolean isInitialized() {
            return this._state == ServiceState.INITIALIZED;
        }

        private void resolve() {
            if (this._state == ServiceState.RESOLVED) {
                return;
            }
            if (this._state != ServiceState.UNDEFINED) {
                throw new InvalidStateException(ServiceState.UNDEFINED.name(), this._state.name());
            }

            // Generate service id
            Attribute attr = StatefulService.this._type.getAnnotation(Attribute.class);
            if (Strings.isNullOrEmpty(StatefulService.this._sid)) {
                if (attr != null && ! Strings.isNullOrEmpty(attr.sid())) {
                    StatefulService.this._sid = attr.sid();
                } else {
                    StatefulService.this._sid = StatefulService.this._type.getName();
                }
            }
            // Set initAtLaunching tag
            if (attr != null && attr.initAtLaunching()) {
                StatefulService.this._initAtLaunching = true;
            } else {
                StatefulService.this._initAtLaunching = false;
            }
            // Resolve dependencies
            Field[] fields = StatefulService.this._type.getDeclaredFields();
            for (Field field : fields) {
                Inject inject = field.getAnnotation(Inject.class);
                if (inject == null) {
                    continue;
                }
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                String setterName = ClassHelper.makeSetterName(fieldName);
                Method setter;
                try {
                    setter = StatefulService.this._type.getMethod(setterName, fieldType);
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new IllegalStateException(
                            StringHelper.makeString("Can't found setter for field {} in class {}, expect the setter name is {}",
                                    fieldName, StatefulService.this._type.getName(), setterName));
                }
                String dependSid = inject.sid();
                if (Strings.isNullOrEmpty(dependSid)) {
                    dependSid = field.getType().getName();
                }
                Dependency dependency = new Dependency(dependSid, fieldType, setter);
                if (StatefulService.this._dependencies.containsKey(dependency.getServiceId())) {
                    throw new KernelException("Duplicated dependency {} in service - {}", dependSid, StatefulService.this._sid);
                }
                StatefulService.this._dependencies.put(dependency.getServiceId(), dependency);
            }

            // Find out init method
            Method[] methods = StatefulService.this._type.getMethods();
            for (Method method : methods) {
                Init init = method.getAnnotation(Init.class);
                if (init == null) {
                    continue;
                }
                if (StatefulService.this._initMethod != null) {
                    throw new KernelException("Do not allow two init method - {} and {} in service {}", 
                            StatefulService.this._initMethod.getName(), method.getName(), StatefulService.this._sid);
                }
                if (method.getParameterCount() > 0) {
                    throw new KernelException("The init method {} in service {} only allow empty parameters",
                            method.getName(), StatefulService.this._sid);
                }
                StatefulService.this._initMethod = method;
            }

            if (StatefulService.this._dependencies.size() == 0 && StatefulService.this._initMethod == null) {
                this._state = ServiceState.INITIALIZED;
            } else {
                this._state = ServiceState.RESOLVED;
            }
        }

        private void satisfy() {
            if (this._state == ServiceState.SATISFIED) {
                return;
            }
            if (this._state != ServiceState.RESOLVED) {
                throw new InvalidStateException(this._state.name(), ServiceState.RESOLVED.name());
            }

            for (Map.Entry<String, Dependency> dependEntry : StatefulService.this._dependencies.entrySet()) {
                Object dependSvc = StatefulService.this._serviceRepo.getService(dependEntry.getKey());
                if (dependSvc == null) {
                    throw new KernelException("Can't retrieve service instance - {}", dependEntry.getKey());
                }
                dependEntry.getValue().setInstance(dependSvc);
            }

            this._state = ServiceState.SATISFIED;
        }

        private void initialize() {
            if (this._state == ServiceState.INITIALIZED) {
                return;
            }
            if (this._state != ServiceState.SATISFIED) {
                throw new InvalidStateException(this._state.name(), ServiceState.SATISFIED.name());
            }

            if (StatefulService.this._initMethod != null) {
                try {
                    StatefulService.this._initMethod.invoke(StatefulService.this._instance);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new KernelException(e, "Invoke init method {} on service {} failed",
                            StatefulService.this._initMethod.getName(), StatefulService.this._sid);
                }
            }
            this._state = ServiceState.INITIALIZED;
        }

//        private void destroy() {
//            this._state = ServiceState.DESTROYED;
//        }
//
//        private boolean isSatisfy() {
//            return this._state == ServiceState.SATISFIED;
//        }
//
//        private ServiceState getState() {
//            return this._state;
//        }
    }
}
