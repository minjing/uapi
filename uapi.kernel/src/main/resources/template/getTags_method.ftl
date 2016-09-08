return new String[] {
<#list tags as tag>
            "${tag}"<#sep>, </#sep>
</#list>
        };