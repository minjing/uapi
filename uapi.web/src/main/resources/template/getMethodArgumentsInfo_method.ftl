uapi.web.ArgumentMapping[] argMappings;
<#list model?keys as key>
        if (method == uapi.web.HttpMethod.${key}) {
            argMappings = new uapi.web.ArgumentMapping[${model[key].argumentMappings?size}];
    <#list model[key].argumentMappings as argMapping>
            argMappings[${argMapping?index}] = new uapi.web.ArgumentMapping(uapi.web.ArgumentMapping.From.${argMapping.from}, "${argMapping.type}");
    </#list>
            return argMappings;
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method: {}", method);