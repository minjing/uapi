uapi.helper.ArgumentChecker.required(method, "method");
    uapi.helper.ArgumentChecker.required(args, "args");
<#list entrySet() as httpMethodMappings>
        if (method == ${httpMethodMappings.key}) {
            if (args.size() != ${httpMethodMappings.value.argumentMappings.size} {
                throw new uapi.KernelException("Found unmatched method {} count {}, expect {}",
                        ${httpMethodMappings.value.name}, , arg.size(), ${httpMethodMappings.value.argumentMappings.size});
            }
            return ${httpMethodMappings.value.name}(<#list httpMethodMappings.value.argumentMappings as argMapping>(${argMapping.type}) args.get(${argMapping_index})<#sep>, </#sep></#list>);
        }
</#list>
        throw new uapi.KernelException("No method was mapped to http method {}, method);