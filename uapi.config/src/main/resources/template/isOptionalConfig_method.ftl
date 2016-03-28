uapi.helper.ArgumentChecker.notEmpty(path, "path");
<#list configInfos as configInfo>
        if ("${configInfo.path}".equals(path)) {
            return ${configInfo.optional};
        }
</#list>
        return false;