return new String[] {
<#list configInfos as configInfo>
            "${configInfo.path}"<#sep>, </#sep>
</#list>
        };