uapi.helper.ArgumentChecker.notEmpty(id, "id");
<#list optionals as optional>
        if ("${optional}".equals(id)) {
            return true;
        }
</#list>
        return false;