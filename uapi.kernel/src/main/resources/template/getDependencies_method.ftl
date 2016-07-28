return new uapi.service.Dependency[] {
<#list dependencies as dependency>
            new uapi.service.Dependency("${dependency.qualifiedServiceId}", ${dependency.serviceType}.class, ${dependency.single?c}, ${dependency.optional?c})<#sep>, </#sep>
</#list>
            };