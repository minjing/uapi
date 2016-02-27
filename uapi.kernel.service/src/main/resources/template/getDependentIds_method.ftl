return new String[] {
<#list dependentIds as dependentId>
            "${dependentId}"<#sep>, </#sep>
</#list>
        };