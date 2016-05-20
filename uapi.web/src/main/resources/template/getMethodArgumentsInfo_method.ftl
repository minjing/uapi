uapi.web.ArgumentMapping[] argMappings;
<#list model?keys as key>
        if (method == uapi.service.web.HttpMethod.${key}) {
            argMappings = new uapi.web.ArgumentMapping[${model[key].argumentMappings?size}];
    <#list model[key].argumentMappings as argMapping>
        <#if "uapi.web.IndexedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.web.IndexedArgumentMapping(uapi.web.ArgumentMapping.From.${argMapping.from}, "${argMapping.type}", ${argMapping.index});
        <#elseif "uapi.web.NamedArgumentMapping" == argMapping.class.name>
            argMappings[${argMapping?index}] = new uapi.web.NamedArgumentMapping(uapi.web.ArgumentMapping.From.${argMapping.from}, "${argMapping.type}", "${argMapping.name}");
        </#if>
    </#list>
            return argMappings;
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method: {}", method);