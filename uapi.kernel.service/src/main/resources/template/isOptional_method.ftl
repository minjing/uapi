uapi.helper.ArgumentChecker.notEmpty(id, "id");
<#list optionals as option>
        if (${optionl}.equals(id)) {
            return true;
        }
</#list>
        return false;