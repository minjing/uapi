uapi.web.restful.ArgumentMapping[] argMappings;
<#list model?keys as key>
        if (method == uapi.web.http.HttpMethod.${key}) {
            argMappings = new uapi.web.restful.ArgumentMapping[${model[key].argumentMappings?size}];
    <#list model[key].argumentMappings as argMapping>
        <#if "uapi.web.restful.IndexedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.web.restful.IndexedArgumentMapping(uapi.web.restful.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index});
        <#elseif "uapi.web.restful.NamedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.web.restful.NamedArgumentMapping(uapi.web.restful.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}");
        </#if>
    </#list>
            return argMappings;
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method: {}", method);