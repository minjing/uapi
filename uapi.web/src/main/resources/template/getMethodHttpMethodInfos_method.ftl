java.util.Map<uapi.service.MethodMeta, List<uapi.service.web.HttpMethod>> methodToHttpMapping = new java.util.HashMap<>();
        java.util.List<uapi.service.web.ArgumentMapping> argMappings = null;
        List<uapi.service.web.HttpMethod> httpMethods = null;
<#list model?keys as methodMeta>
            argMappings = new java.util.LinkedList<>();
    <#list methodMeta.argumentMappings as argMapping>
        <#if "uapi.service.web.IndexedArgumentMapping" == argMapping.class.name>
            uapi.service.web.Argumentmapping argMapping = new uapi.service.web.IndexedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index});
        <#elseif "uapi.service.web.NamedArgumentMapping" == argMapping.class.name>
            uapi.service.web.Argumentmapping argMapping = new uapi.service.web.NamedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}");
        </#if>
            argMappings.add(argMapping);
    </#list>
            uapi.service.MethodMeta methodMeta = new uapi.service.MethodMeta(${methodMeta.name}, ${methodMeta.returnTypeName}, argMappings);
            httpMethods = new java.util.LinkedList<>();
    <#list model.get(methodMeta) as httpMethod>
            httpMethods.add(uapi.service.web.HttpMethod.${httpMethod});
    </#list>

            methodToHttpMapping.put(methodMeta, httpMethods);
</#list>
            return methodToHttpMapping;