package uapi.web.internal;

import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by xquan on 6/6/2016.
 */
public class MethodInfo {

    private final String _name;
    private final String[] _argTypes;
    private final String _rtnType;

    public MethodInfo(
            final String name,
            final List<String> argumentTypes,
            final String returnType) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(argumentTypes, "argumentTypes");
        ArgumentChecker.required(returnType, "returnType");
        this._name = name;
        this._argTypes = argumentTypes.toArray(new String[argumentTypes.size()]);
        this._rtnType = returnType;
    }

    public String getName() {
        return this._name;
    }

    public String[] getArgumentTypes() {
        return this._argTypes;
    }

    public String getReturnType() {
        return this._rtnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodInfo methodInfo = (MethodInfo) o;

        if (!_name.equals(methodInfo._name)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(_argTypes, methodInfo._argTypes)) return false;
        return _rtnType.equals(methodInfo._rtnType);

    }

    @Override
    public int hashCode() {
        int result = _name.hashCode();
        result = 31 * result + Arrays.hashCode(_argTypes);
        result = 31 * result + _rtnType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return StringHelper.makeString(
                "MethodInfo[name={}, returnType={}, arguments={}",
                this._name, this._rtnType, CollectionHelper.asString(this._argTypes));
    }
}
