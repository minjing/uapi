package uapi.injector;

import uapi.service.IService;

/**
 * The service can be inject other services
 */
public interface IInjectable extends IService {

    /**
     * Return dependent service classes list
     *
     * @return  Dependent service classes list
     */
    Class<?>[] getDependentClasses();

    /**
     * Inject specific service to this service
     *
     * @param service   The specific service which is this service depends on
     */
    void inject(Object service);
}
