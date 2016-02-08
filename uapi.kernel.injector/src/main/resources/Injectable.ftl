package ${packageName}

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.ArrayList;

import uapi.IService;
import uapi.helper.ArgumentChecker;
import uapi.injector.IInjectable;

@AutoService(IService.class)
public final class ${className} extends ${superClassName} implements IInjectable {

    private final String _svcId;

    public ${className}() {
        this._svcId = "${className}";
    }

<#list fieldMetas as fieldMeta>
    <#if fieldMeta.isList() || fieldMeta.isSet()>
    public void add${fieldMeta.fieldName}(${fieldMeta.fieldTypeName} obj) {
        this.${fieldMeta.fieldName}.add(obj);
    }
    <#else>
    public void set${fieldMeta.fieldName}(${fieldMeta.fieldTypeName} obj) {
        this.${fieldMeta.fieldName} = obj;
    }
    </#if>
</#list>

    @Override
    public List<String> getDependencies() {
        List<String> dependencies = new ArrayList<>();
<#list fieldMetas as fieldMeta>
        dependencies.add(${fieldMetas.getFieldTypeName());
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
            serviceId = service.getClass.getName();
        }
        switch serviceId {
<#list fieldMetas as fieldMeta>
        case "$fieldMeta.getInjectServiceId()":
     <#if fieldMeta.isList() || fieldMeta.isSet()>
            add${fieldMeta.fieldName}(service);
     <#else>
            set${fieldMeta.fieldName}(service);
     </#if>
            break;
</#list>
        default:
            throw new KernelException(
                "The service {} with id {} can't be injected into service {}",
                service.getClass().getName, serviceId, this.getClass().getName);
        }
    }
}