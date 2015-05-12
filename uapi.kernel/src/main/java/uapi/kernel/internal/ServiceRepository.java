package uapi.kernel.internal;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

import uapi.kernel.IService;
import uapi.kernel.InvalidArgumentException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.KernelException;

public final class ServiceRepository {

    private final Map<String, StatefulService> _unSatisfiedServices;
    private final Map<String, StatefulService> _satisfiedServices;

    public ServiceRepository() {
        this._unSatisfiedServices = new HashMap<>();
        this._satisfiedServices = new HashMap<>();
    }

    public void addService(IService service) {
        if (service == null) {
            throw new InvalidArgumentException("service", InvalidArgumentType.EMPTY);
        }
        StatefulService svc = new StatefulService(this, service);
        if (this._unSatisfiedServices.containsKey(svc.getId())) {
            throw new KernelException("The service whit the sid {} was registered in the repository.", svc.getId());
        }
        this._unSatisfiedServices.put(svc.getId(), svc);
    }

    IService getService(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getService(serviceType.getName());
    }

    IService getService(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        StatefulService service = this._satisfiedServices.get(serviceId);
        if (service != null) {
            return service.getInstance();
        }
        service = this._unSatisfiedServices.get(serviceId);
        if (service == null) {
            throw new KernelException("Can't found specific service in the repository - ", serviceId);
        }
        return service.getInstance();
    }
}
