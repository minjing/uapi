package ${packageName};

<#list imports as import>
${import};
</#list>

<#list annotations as annotation>
@${annotation.name}(<#list annotation.arguments as argument>${argument.name}=<#if argument.isString>"${argument.value}"<#else>${argument.value}</#if></#list>)
</#list>
public final class ${generatedClassName}
extends ${className}
<#list implements>implements <#items as implement>${implement}<#sep>, </#sep></#items></#list> {

<#list fields as field>
    <#if field.isList>
    ${field.modifiers} java.util.List<${field.typeName}> ${field.name} = new java.util.ArrayList<>();
    <#else>
    ${field.modifiers} ${field.typeName} ${field.name};
    </#if>

</#list>

<#list methods as method>
    <#list method.annotations as annotation>
    @${annotation.name}<#list annotation.arguments>(<#items as argument>${argument.name}=<#if argument.isString>"${argument.value}"<#else>${argument.value}</#if></#items>)</#list>
    </#list>
    ${method.modifiers} ${method.returnTypeName} ${method.name} (
    <#list method.parameters as parameter>
            ${parameter.modifiers} ${parameter.type} ${parameter.name}<#sep>, </#sep>
    </#list>
    ) <#list method.throwTypeNames>throws <#items as throw>${throw}<#sep>, </#sep></#items></#list> {
    <#if method.invokeSuperBefore>
        super.${method.name}(<#list method.parameters as parameter>${parameter.name}<#sep>, </#sep></#list>);
    </#if>
    <#list method.codes as code>
        ${code.code}
    </#list>
    <#if method.invokeSuperAfter>
        super.${method.name}(<#list method.parameters as parameter>${parameter.name}<#sep>, </#sep></#list>);
    </#if>
    }

</#list>
}