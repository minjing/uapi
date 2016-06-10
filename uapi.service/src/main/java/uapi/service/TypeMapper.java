package uapi.service;

import uapi.Type;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TypeMapper {

    private final Map<String, Class<?>> _typeMapping;

    public TypeMapper() {
        this._typeMapping = new HashMap<>();
        this._typeMapping.put(Type.BOOLEAN, Type.T_BOOLEAN);
        this._typeMapping.put(Type.INTEGER, Type.T_INTEGER);
        this._typeMapping.put(Type.SHORT, Type.T_SHORT);
        this._typeMapping.put(Type.LONG, Type.T_LONG);
        this._typeMapping.put(Type.FLOAT, Type.T_FLOAT);
        this._typeMapping.put(Type.DOUBLE, Type.T_DOUBLE);

        this._typeMapping.put(Type.Q_BOOLEAN, Type.T_BOOLEAN);
        this._typeMapping.put(Type.Q_INTEGER, Type.T_INTEGER);
        this._typeMapping.put(Type.Q_SHORT, Type.T_SHORT);
        this._typeMapping.put(Type.Q_LONG, Type.T_LONG);
        this._typeMapping.put(Type.Q_FLOAT, Type.T_FLOAT);
        this._typeMapping.put(Type.Q_DOUBLE, Type.T_DOUBLE);

        this._typeMapping.put(Type.STRING, Type.T_STRING);
        this._typeMapping.put(Type.Q_STRING, Type.T_STRING);
    }

    public Class<?> getType(String typeName) {
        ArgumentChecker.required(typeName, "typeName");
        return this._typeMapping.get(typeName);
    }

    public void register(String typeName, Class<?> type) {
        ArgumentChecker.required(typeName, "typeName");
        ArgumentChecker.required(type, "type");
        this._typeMapping.put(typeName, type);
    }
}
