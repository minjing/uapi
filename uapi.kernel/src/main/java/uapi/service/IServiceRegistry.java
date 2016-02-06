package uapi.service;

/**
 * A service registry hold and mange all services
 */
public interface IServiceRegistry {

    /**
     * Find specific service based on specified service type
     * from the service registry
     *
     * @param   serviceType
     *          The specific service type
     * @param   <T>
     *          The service type
     * @return  The specific service instance or null
     */
    <T> T findService(Class<T> serviceType);
}
