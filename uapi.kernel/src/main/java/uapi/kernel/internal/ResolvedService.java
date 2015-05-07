package uapi.kernel.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

import com.google.common.base.Strings;

import uapi.kernel.Attribute;
import uapi.kernel.IService;
import uapi.kernel.Inject;
import uapi.kernel.InvalidArgumentException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.helper.ClassHelper;
import uapi.kernel.helper.StringHelper;
import uapi.kernel.KernelException;

final class ResolvedService {

    private final String                    _sid;
    private final Class<? extends IService> _type;
    private final Map<String, Dependency>   _dependencies;
    private final IService                  _instance;

    ResolvedService(Class<? extends IService> type) {
        if (type == null) {
            throw new IllegalArgumentException("The argument is required - type");
        }

        Attribute attr = type.getAnnotation(Attribute.class);
        if (attr != null && ! Strings.isNullOrEmpty(attr.sid())) {
            this._sid = attr.sid();
        } else {
            this._sid = type.getName();
        }
        // Resolve dependencies
        Field[] fields = type.getDeclaredFields();
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
                setter = type.getMethod(setterName, fieldType);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(
                        StringHelper.makeString("Can't found setter for field {} in class {}", fieldName, type.getName()));
            }
            String dependSid = inject.sid();
            if (Strings.isNullOrEmpty(dependSid)) {
                dependSid = field.getType().getName();
            }
            Dependency dependency = new Dependency(dependSid, fieldType, setter);
            if (this._dependencies.containsKey(dependency.getServiceId())) {
                throw new KernelException("The dependency {} was not exist in service - {}", type.getName(), dependSid);
            }
            this._dependencies.put(dependency.getServiceId(), dependency);
        }

        // Construct service instance
        Constructor<? extends IService> constructor;
        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new KernelException(e, "Do not found default constructor in service - ", this._sid);
        }
        try {
            this._instance = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new KernelException(e, "Create service instance failed - ", this._sid);
        }
        this._type = type;
        this._dependencies = new HashMap<>();
    }

    String getId() {
        return this._sid;
    }

    Class<? extends IService> getType() {
        return this._type;
    }

    IService getInstance() {
        if (this._dependencies.size() > 0) {
            throw new KernelException("The service is not satisfied - {}", this._sid);
        }
        return this._instance;
    }

    void addDependency(String serviceId, Class<?> serviceType, Method setter) {
        
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
                this._setter.invoke(ResolvedService.this._instance, instance);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new KernelException(e, "Set dependency {} failed - {}", this._setter.getName(), ResolvedService.this._sid);
            }
            ResolvedService.this._dependencies.remove(this._sid);
        }
    }
}
