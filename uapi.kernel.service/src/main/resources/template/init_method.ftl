return new String[] {
<#list serviceIds as serviceId>
            "${serviceId}"<#sep>, </#sep>
</#list>
        };