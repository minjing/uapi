uapi.service.web.ArgumentMapping[] argMappings;
<#list model?keys as key>
        if (method == uapi.service.web.HttpMethod.${key}) {
            argMappings = new uapi.service.web.ArgumentMapping[${model[key].argumentMappings?size}];
    <#list model[key].argumentMappings as argMapping>
        <#if "uapi.service.web.IndexedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.service.web.IndexedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index});
        <#elseif "uapi.service.web.NamedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.service.web.NamedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}");
        </#if>
    </#list>
            return argMappings;
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method: {}", method);