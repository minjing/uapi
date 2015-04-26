package uapi.kernel.internal;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.HashMap;

import uapi.kernel.IService;

final class ResolvedService {

    private final Class<? extends IService>     _type;
    private final Map<String, Dependency>       _dependencies;
    private IService                            _instance;

    ResolvedService(Class<? extends IService> type) {
        if (type == null) {
            throw new IllegalArgumentException("The argument is required - type");
        }
        this._type = type;
        this._dependencies = new HashMap<>();
    }

    private final class Dependency {

        private final Class<? extends IService> _dependentType;
        private final PropertyDescriptor        _propDesc;

        private Dependency(Class<? extends IService> type, PropertyDescriptor propertyDescriptor) {
            if (type == null) {
                throw new IllegalArgumentException("The argument is required - type");
            }
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("The argument is required - propertyDescriptor");
            }
            this._dependentType = type;
            this._propDesc = propertyDescriptor;
        }
    }
}
