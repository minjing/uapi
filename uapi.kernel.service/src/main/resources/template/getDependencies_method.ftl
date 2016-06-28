return new uapi.service.Dependency[] {
<#list dependencies as dependency>
            new uapi.service.Dependency("${dependency.leftValue}", ${dependency.rightValue}.class)<#sep>, </#sep>
</#list>
            };