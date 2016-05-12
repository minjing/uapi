uapi.web.ArgumentMapping[] argMappings;
<#list entrySet() as mappedArgMappingsEntry>
        if (method == ${mappedArgMappingsEntry.key} {
            uapi.web.HttpMethod httpMethod = argMappingsEntry.getKey();
            argMappings = new uapi.web.ArgumentMapping[${mappedArgMappingsEntry.value.size}];
    <#list mappedArgMappingsEntry.value.argumentMappings as argMapping>
                argMappings[${argMapping?index}] = new uapi.web.ArgumentMapping(uapi.web.${argMapping.from}, ${argMapping.type});
    </#list>
            return argMappings;
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method: {}", method);