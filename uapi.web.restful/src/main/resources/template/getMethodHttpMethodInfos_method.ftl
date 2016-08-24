java.util.Map<uapi.service.ServiceMeta, java.util.List<uapi.web.HttpMethod>> methodToHttpMapping = new java.util.HashMap<>();
        java.util.List<uapi.service.ArgumentMeta> argMappings = null;
        java.util.List<uapi.web.http.HttpMethod> httpMethods = null;
<#list model?keys as svcMethodMeta>
            argMappings = new java.util.LinkedList<>();
    <#list svcMethodMeta.argumentMappings as argMapping>
        <#if "uapi.web.IndexedArgumentMapping" == argMapping.class.name>
            argMappings.add(new uapi.web.IndexedArgumentMapping(uapi.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index}));
        <#elseif "uapi.web.NamedArgumentMapping" == argMapping.class.name>
            argMappings.add(new uapi.web.NamedArgumentMapping(uapi.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}"));
        </#if>
    </#list>
            uapi.service.ServiceMeta svcMethodMeta = new uapi.service.ServiceMeta("${svcMethodMeta.name}", "${svcMethodMeta.returnTypeName}", argMappings);
            svcMethodMeta.setId("${svcMethodMeta.id}");
            httpMethods = new java.util.LinkedList<>();
    <#list model?values[svcMethodMeta_index] as httpMethod>
            httpMethods.add(uapi.web.http.HttpMethod.${httpMethod});
    </#list>

            methodToHttpMapping.put(svcMethodMeta, httpMethods);
</#list>
            return methodToHttpMapping;