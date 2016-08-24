<#list model?keys as key>
        if (method == uapi.web.http.HttpMethod.${key}) {
            return "${model[key].returnTypeName}";
        }
</#list>
        throw new uapi.KernelException("No method is mapped to http method {}", method);