package uapi.injector;

import uapi.helper.StringHelper;

public final class Setter {

    private String _className;
    private String _superClassName;
    private String _fieldName;
    private String _fieldTypeName;

    public void setClassName(String className) {
        this._className = className;
    }

    public String getClassName() {
        return this._className;
    }

    public void setSuperClassName(String className) {
        this._superClassName = className;
    }

    public String getSuperClassName() {
        return this._superClassName;
    }

    public void setFieldName(String fieldName) {
        this._fieldName = fieldName;
    }

    public String getFieldName() {
        return this._fieldName;
    }

    public void setFieldTypeName(String typeName) {
        this._fieldTypeName = typeName;
    }

    public String getFieldTypeName() {
        return this._fieldTypeName;
    }

    public boolean isList() {
        return false;
    }

    public boolean isSet() {
        return false;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("className={}\n superClassName={}\n fieldName={}\n fieldTypeName={}\n",
                this._className, this._superClassName, this._fieldName, this._fieldTypeName);
    }
}
