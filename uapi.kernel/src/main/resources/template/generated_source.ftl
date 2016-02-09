package ${packageName};

<#list imports as import>
${import};
</#list>

<#list annotations as annotation>
${annotation.name}(<#list annotation.arguments as argument>${argument.name}=<#if argument.isString>"${argument.value}"<#else>${argument.value}<#/if></#list>)
</#list>
public final class ${generatedClassName} extends ${className}
implements <#list implements as implement>${implement}<#sep>, </#sep></#list> {

<#list properties as property>
    <#if property.isList>
    private java.util.List<${property.typeName}> ${property.name} = new ArrayList<>();
    public void add${property.name}(${property.typeName} ${property.name}) {
        <#if property.notNull>
        uapi.helper.ArgumentChecker.notNull(${property.name}, "${property.name}");
        </#if>
        this.${property.name}.add(${property.name});
    }
    <#else>
    private ${property.typeName} ${property.name};
    public void set${property.name}(${property.type}) {
        <#if property.notNull>
        uapi.helper.ArgumentChecker.notNull(${property.name}, "${property.name}");
        </#if>
        this.${property.name} = ${property.name};
    }
    </#if>

</#list>

<#list methods as method>
    ${method.modify} <#if method.isStatic>static</#if> ${method.returnType} (
    <#list method.parameters as parameter>
            ${parameter.type} ${parameter.name}<#sep>, </#sep>
    </#list>
    ) <#list method.throws>throws<items as throw>${throw}<#sep>, </#sep></#items></#list> {
    <#list method.parameters as parameter>
        <#if parameter.notNull>
            uapi.helper.ArgumentChecker.notNull(${parameter.name}, "${parameter.name}");
        </#if>
    </#list>
    <#list method.codes as code>
        ${code}
    </#list>
        super.${method.name}(<#list method.parameters as parameter>${parameter.name}<#sep>, </#sep></#list>);
    }
</#list>
}