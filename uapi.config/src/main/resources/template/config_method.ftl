uapi.helper.ArgumentChecker.notEmpty(path, "path");
uapi.helper.ArgumentChecker.notNull(configObject, "configObject");
<#list configInfos as configInfo>
        if (path.equals("${configInfo.path}")) {
            if (! (configObject instanceof ${configObject.fieldType})) {
                throw new uapi.InvalidArgumentException(
                        "The config object {} can't be converted to {}",
                        configObject, "${configObject.fieldType}");
            }
            this.${configInfo.fieldName} = (${configInfo.fieldType}) configObject;
            return;
        }
</#list>
        throw new uapi.KernelException("Can't set config object {} into service {}", configObject, this);