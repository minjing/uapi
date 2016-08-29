uapi.helper.ArgumentChecker.required(method, "method");
        uapi.helper.ArgumentChecker.required(args, "args");
<#list model?keys as key>
        if (method == uapi.web.http.HttpMethod.${key}) {
            if (args.size() != ${model[key].argumentMetas?size}) {
                throw new uapi.KernelException("Found unmatched method {} argument count {}, expect {}",
                        "${model[key].name}", args.size(), ${model[key].argumentMetas?size});
            }
            return ${model[key].name}(<#list model[key].argumentMetas as argMapping>(${argMapping.type}) args.get(${argMapping_index})<#sep>, </#sep></#list>);
        }
</#list>
        throw new uapi.KernelException("No method was mapped to http method {}", method);