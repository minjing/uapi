uapi.helper.ArgumentChecker.required(method, "method");
        uapi.helper.ArgumentChecker.required(args, "args");
<#list model?keys as key>
        if (method == uapi.service.web.HttpMethod.${key}) {
            if (args.size() != ${model[key].argumentMappings?size}) {
                throw new uapi.KernelException("Found unmatched method {} argument count {}, expect {}",
                        "${model[key].name}", args.size(), ${model[key].argumentMappings?size});
            }
            return ${model[key].name}(<#list model[key].argumentMappings as argMapping>(${argMapping.type}) args.get(${argMapping_index})<#sep>, </#sep></#list>);
        }
</#list>
        throw new uapi.KernelException("No method was mapped to http method {}", method);