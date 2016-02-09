package ${packageName};

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.ArrayList;

import uapi.IService;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.injector.IInjectable;

@AutoService(IService.class)
public final class ${generatedClassName} extends ${className} implements IInjectable {

    private final String _svcId;

    public ${generatedClassName}() {
        this._svcId = "${packageName}.${className}";
    }

    @Override
    public String getServiceId() {
        return this._svcId;
    }

<#list fields as field>
    <#if field.isList>
    public void add${field.name}(${field.typeName} obj) {
        this.${field.name}.add(obj);
    }
    <#else>
    public void set${field.name}(${field.typeName} obj) {
        this.${field.name} = obj;
    }
    </#if>
</#list>

    @Override
    public List<String> getDependencies() {
        List<String> dependencies = new ArrayList<>();
<#list fields as field>
        dependencies.add("${field.typeName}");
</#list>
        return dependencies;
    }

    @Override
    public void inject(Object service) {
        ArgumentChecker.required(service, "service");

        String serviceId;
        if (service instanceof IService) {
            serviceId = ((IService) service).getServiceId();
        } else {
            serviceId = service.getClass().getName();
        }
        switch (serviceId) {
<#list fields as field>
        case "${field.injectServiceId}":
     <#if field.isList>
            add${field.name}((${field.typeName}) service);
     <#else>
            set${field.name}((${field.typeName}) service);
     </#if>
            break;
</#list>
        default:
            throw new KernelException(
                "The service {} with id {} can't be injected into service {}",
                service.getClass().getName(), serviceId, this.getClass().getName());
        }
    }
}