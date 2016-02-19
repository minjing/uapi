    void injectObject(
        final uapi.injector.Injection injection
    ) throws uapi.InvalidArgumentException, uapi.KernelException {
<#list setterBuilders as setter>
        if (injection.getId().equals(${setter.injectId})) {
            injection.checkType(${setter.injectType}.class);
            ${setter.name}((${setter.injectType}) injection.getObject());
            return;
        }
</#list>
        throw new KernelException("Can't inject object {} into service {}", injection, this);

    }