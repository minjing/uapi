uapi.service.web.ArgumentMapping[] argMappings;
<#list model?keys as key>
        if (method == uapi.web.HttpMethod.${key}) {
            argMappings = new uapi.service.web.ArgumentMapping[${model[key].argumentMappings?size}];
    <#list model[key].argumentMappings as argMapping>
        <#if "uapi.web.IndexedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.web.IndexedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", ${argMapping.index});
        <#elseif "uapi.web.NamedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.web.NamedArgumentMapping(uapi.service.web.ArgumentFrom.${argMapping.from}, "${argMapping.type}", "${argMapping.name}");
        </#if>
    </#list>
            return argMappings;
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method: {}", method);