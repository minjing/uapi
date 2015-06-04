package uapi.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

import com.google.common.base.Strings;

import uapi.InvalidArgumentException;
import uapi.InvalidStateException;
import uapi.KernelException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.helper.ChangeableBoolean;
import uapi.helper.ClassHelper;
import uapi.helper.StringHelper;
import uapi.service.Inject;
import uapi.service.OnInit;

final class StatefulService {

    private final ServiceRepository         _serviceRepo;
    private final Class<?>                  _type;
    private final Object                    _instance;
    private final Map<String, Dependency>   _dependencies;
    private final Lifecycle                 _lifecycle;

    private String      _name;
    private boolean     _lazyInit;
    private Method      _initMethod;

    StatefulService(ServiceRepository serviceRepository, Object instance) {
        this(serviceRepository, instance, null);
    }

    StatefulService(ServiceRepository serviceRepository, Object instance, String name) {
        if (serviceRepository == null) {
            throw new InvalidArgumentException("serviceRepository", InvalidArgumentType.EMPTY);
        }
        if (instance == null) {
            throw new InvalidArgumentException("type", InvalidArgumentType.EMPTY);
        }

        if (! Strings.isNullOrEmpty(name)) {
            this._name = name;
        }
        this._serviceRepo = serviceRepository;
        this._type = instance.getClass();
        this._instance = instance;
        this._dependencies = new HashMap<>();
        this._lifecycle = new Lifecycle();
        this._lifecycle.resolve();
    }

    String getName() {
        return this._name;
    }

    Class<?> getType() {
        return this._type;
    }

    boolean isInitialized() {
        return this._lifecycle.isInitialized();
    }

    boolean isLazyInit() {
        return this._lazyInit;
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

        private final String    _name;
        private final Class<?>  _type;
        private final Method    _setter;
        private final boolean   _multiple;

        Dependency(String sid, Class<?> type, Method setter, boolean multiple) {
            if (type == null) {
                throw new InvalidArgumentException("type", InvalidArgumentType.EMPTY);
            }
            if (setter == null) {
                throw new InvalidArgumentException("setter", InvalidArgumentType.EMPTY);
            }
            if (sid != null) {
                this._name = sid;
            } else {
                this._name = type.getName();
            }
            this._type = type;
            this._setter = setter;
            this._multiple = multiple;
        }

        String getName() {
            return this._name;
        }

        Class<?> getType() {
            return this._type;
        }

        boolean isMultiple() {
            return this._multiple;
        }

        void setInstance(Object instance) {
            if (instance == null) {
                throw new InvalidArgumentException("instance", InvalidArgumentType.EMPTY);
            }
            try {
                this._setter.invoke(StatefulService.this._instance, instance);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new KernelException(e, "Set dependency {} failed - {}", this._setter.getName(), StatefulService.this._name);
            }
            StatefulService.this._dependencies.remove(this._name);
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
            if (this._state == ServiceState.RESOLVED || this._state == ServiceState.INITIALIZED) {
                return;
            }
            if (this._state != ServiceState.UNDEFINED) {
                throw new InvalidStateException(ServiceState.UNDEFINED.name(), this._state.name());
            }

            // Resolve dependencies
            Field[] fields = StatefulService.this._type.getDeclaredFields();
            for (Field field : fields) {
                Inject inject = field.getAnnotation(Inject.class);
                if (inject == null) {
                    continue;
                }
                String fieldName = field.getName();
                ChangeableBoolean isCollection = new ChangeableBoolean();
                Class<?> fieldType = ClassHelper.getElementType(field.getType(), field.getGenericType(), isCollection);
                String setterName = ClassHelper.makeSetterName(fieldName, isCollection.get());
                Method setter;
                try {
                    setter = StatefulService.this._type.getMethod(setterName, fieldType);
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new IllegalStateException(
                            StringHelper.makeString("Can't found setter for field {} in class {}, expect the setter name is {}",
                                    fieldName, StatefulService.this._type.getName(), setterName));
                }
                String dependName = inject.name();
                if (Strings.isNullOrEmpty(dependName)) {
                    dependName = fieldType.getName();
                }
                Dependency dependency = new Dependency(dependName, fieldType, setter, isCollection.get());
                if (StatefulService.this._dependencies.containsKey(dependency.getName())) {
                    throw new KernelException("Duplicated dependency {} in service - {}", dependName, StatefulService.this._name);
                }
                StatefulService.this._dependencies.put(dependency.getName(), dependency);
            }

            // Find out init method
            Method[] methods = StatefulService.this._type.getMethods();
            for (Method method : methods) {
                OnInit init = method.getAnnotation(OnInit.class);
                if (init == null) {
                    continue;
                }
                if (StatefulService.this._initMethod != null) {
                    throw new KernelException("Do not allow two init method - {} and {} in service {}",
                            StatefulService.this._initMethod.getName(), method.getName(), StatefulService.this._name);
                }
                if (method.getParameterCount() > 0) {
                    throw new KernelException("The init method {} in service {} only allow empty parameters",
                            method.getName(), StatefulService.this._name);
                }
                StatefulService.this._initMethod = method;
                StatefulService.this._lazyInit = init.lazy();
            }

            if (StatefulService.this._dependencies.size() == 0 && StatefulService.this._initMethod == null) {
                this._state = ServiceState.INITIALIZED;
            } else {
                this._state = ServiceState.RESOLVED;
            }
        }

        private void satisfy() {
            if (this._state == ServiceState.SATISFIED || this._state == ServiceState.INITIALIZED) {
                return;
            }
            if (this._state != ServiceState.RESOLVED) {
                throw new InvalidStateException(this._state.name(), ServiceState.RESOLVED.name());
            }

            for (Map.Entry<String, Dependency> dependEntry : StatefulService.this._dependencies.entrySet()) {
                if (dependEntry.getValue().isMultiple()) {
                    Object[] dependSvcs = StatefulService.this._serviceRepo.getServices(dependEntry.getKey());
                    for (Object dependSvc : dependSvcs) {
                        dependEntry.getValue().setInstance(dependSvc);
                    }
                } else {
                    Object dependSvc = StatefulService.this._serviceRepo.getService(dependEntry.getKey());
                    if (dependSvc == null) {
                        throw new KernelException("Can't retrieve service instance - {}", dependEntry.getKey());
                    }
                    dependEntry.getValue().setInstance(dependSvc);
                }
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
                            StatefulService.this._initMethod.getName(), StatefulService.this._name);
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
