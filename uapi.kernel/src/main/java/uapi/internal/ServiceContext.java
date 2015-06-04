package uapi.internal;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.service.IServiceContext;
import uapi.service.Inject;
import uapi.service.Registration;
import uapi.service.Type;

@Registration({
    @Type(IServiceContext.class)
})
public class ServiceContext implements IServiceContext {

    @Inject
    private ServiceRepository _serviceRepository;

    public void setServiceRepository(ServiceRepository serviceRepository) {
        if (serviceRepository == null) {
            throw new InvalidArgumentException("serviceRepository", InvalidArgumentType.EMPTY);
        }
        this._serviceRepository = serviceRepository;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        return this._serviceRepository.getService(serviceType);
    }

    @Override
    public <T> T[] getServices(Class<T> serviceType) {
        return this._serviceRepository.getServices(serviceType);
    }

    @Override
    public <T> T getService(String serviceId) {
        return this._serviceRepository.getService(serviceId);
    }

    @Override
    public <T> T[] getServices(String serviceId) {
        return this._serviceRepository.getServices(serviceId);
    }

}
