package uapi.service;

import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.web.restful.ArgumentMapping;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xquan on 6/21/2016.
 */
public class MethodMeta {

    private final String _name;
    private final String _returnTypeName;
    private List<ArgumentMeta> _argMetas;

    public MethodMeta(
            final String name,
            final String returnTypeName
    ) {
        this(name, returnTypeName, null);
    }

    public MethodMeta(
            final String name,
            final String returnTypeName,
            final List<ArgumentMeta> argMappings) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(returnTypeName, "valueParserName");
        this._name = name;
        this._returnTypeName = returnTypeName;
        this._argMetas = new LinkedList<>();
        if (argMappings != null) {
            this._argMetas.addAll(argMappings);
        }
    }

    public String getName() {
        return this._name;
    }

    public String getReturnTypeName() {
        return this._returnTypeName;
    }

    public void addArgumentMeta(
            final ArgumentMeta argMeta
    ) {
        ArgumentChecker.required(argMeta, "argMeta");
        this._argMetas.add(argMeta);
    }

    public List<ArgumentMeta> getArgumentMetas() {
        return this._argMetas;
    }

    public boolean isSame(MethodMeta other) {
        if (other == null) {
            return false;
        }
        if (! this._name.equals(other._name)) {
            return false;
        }
        if (! this._returnTypeName.equals(other._returnTypeName)) {
            return false;
        }
        if (this._argMetas.size() != other._argMetas.size()) {
            return false;
        }
        for (int i = 0; i < this._argMetas.size(); i++) {
            if (! this._argMetas.get(i).isSameType(other._argMetas.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodMeta that = (MethodMeta) o;

        if (_name != null ? !_name.equals(that._name) : that._name != null) return false;
        if (_returnTypeName != null ? !_returnTypeName.equals(that._returnTypeName) : that._returnTypeName != null)
            return false;
        return _argMetas != null ? _argMetas.equals(that._argMetas) : that._argMetas == null;

    }

    @Override
    public int hashCode() {
        int result = _name != null ? _name.hashCode() : 0;
        result = 31 * result + (_returnTypeName != null ? _returnTypeName.hashCode() : 0);
        result = 31 * result + (_argMetas != null ? _argMetas.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("{}[{}]", this.getClass().getSimpleName(), propertiesString());
    }

    public String propertiesString() {
        return StringHelper.makeString("name={},returnTypeName={},arguments={}",
                this._name, this._returnTypeName, CollectionHelper.asString(this._argMetas));
    }
}
