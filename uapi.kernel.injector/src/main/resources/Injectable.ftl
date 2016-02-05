package ${packageName}

public final class ${className} extends ${superClassName} {

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
}