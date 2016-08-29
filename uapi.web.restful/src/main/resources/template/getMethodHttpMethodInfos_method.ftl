java.util.Map<uapi.service.ServiceMeta, java.util.List<uapi.web.http.HttpMethod>> methodToHttpMapping = new java.util.HashMap<>();
        java.util.List<uapi.service.ArgumentMeta> argMappings = null;
        java.util.List<uapi.web.http.HttpMethod> httpMethods = null;
<#list model?keys as svcMethodMeta>
            argMappings = new java.util.LinkedList<>();
    <#list svcMethodMeta.argumentMetas as argMapping>
        <#if "uapi.web.restful.IndexedArgumentMapping" == argMapping.class.name>
            argMappings.add(new uapi.web.restful.IndexedArgumentMapping(uapi.web.restful.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index}));
        <#elseif "uapi.web.restful.NamedArgumentMapping" == argMapping.class.name>
            argMappings.add(new uapi.web.restful.NamedArgumentMapping(uapi.web.restful.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}"));
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