package ${packageName}

import uapi.injector.IInjectable;

public final class ${className} extends ${superClassName} implements IInjectable {

    public ${className}() { }

<#list setters as setter>
    <#if setter.isList() || setter.isSet()>
    public void add${setter.getName()}(${setter.fieldTypeName} obj) {
        this.${setter.fieldName}.add(obj);
    }
    <#else>
    public void set${setter.fieldName()}(${setter.fieldTypeName} obj) {
        this.${setter.fieldName} = obj;
    }
    </#if>
</#list>

    @Override
    public Class<?>[] getDependentClasses() {
<#list setters as setter>

</#list>
        return null;
    }

    @Override
    public void inject(Object service) {
    }
}