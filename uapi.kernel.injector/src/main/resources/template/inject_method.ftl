uapi.helper.ArgumentChecker.notNull(injection, "injection");
<#list setters as setter>
        if (injection.getId().equals("${setter.injectId}")) {
            if (! (injection.getObject() instanceof ${setter.injectType})) {
                throw new uapi.InvalidArgumentException(
                        "The injected object {} can't be converted to {}",
                        injection.getObject(), "${setter.injectType}");
            }
            // injection.checkType(${setter.injectType}.class);
            ${setter.name}((${setter.injectType}) injection.getObject());
            return;
        }
</#list>
        throw new uapi.KernelException("Can't inject object {} into service {}", injection, this);