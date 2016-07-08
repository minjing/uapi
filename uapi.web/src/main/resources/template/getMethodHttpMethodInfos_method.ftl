java.util.Map<uapi.service.ServiceMeta, java.util.List<uapi.service.web.HttpMethod>> methodToHttpMapping = new java.util.HashMap<>();
        java.util.List<uapi.service.ArgumentMeta> argMappings = null;
        java.util.List<uapi.service.web.HttpMethod> httpMethods = null;
<#list model?keys as svcMethodMeta>
            argMappings = new java.util.LinkedList<>();
    <#list svcMethodMeta.argumentMappings as argMapping>
        <#if "uapi.service.web.IndexedArgumentMapping" == argMapping.class.name>
            argMappings.add(new uapi.service.web.IndexedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index}));
        <#elseif "uapi.service.web.NamedArgumentMapping" == argMapping.class.name>
            argMappings.add(new uapi.service.web.NamedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}"));
        </#if>
    </#list>
            uapi.service.ServiceMeta svcMethodMeta = new uapi.service.ServiceMeta("${svcMethodMeta.name}", "${svcMethodMeta.returnTypeName}", argMappings);
            svcMethodMeta.setId("${svcMethodMeta.id}");
            httpMethods = new java.util.LinkedList<>();
    <#list model?values[svcMethodMeta_index] as httpMethod>
            httpMethods.add(uapi.service.web.HttpMethod.${httpMethod});
    </#list>

            methodToHttpMapping.put(svcMethodMeta, httpMethods);
</#list>
            return methodToHttpMapping;