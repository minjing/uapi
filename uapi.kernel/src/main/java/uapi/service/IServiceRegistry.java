package uapi.service;

/**
 * A service registry hold and mange all services
 */
public interface IServiceRegistry {

    /**
     * Find specific service based on specified service type
     * from the service registry
     *
     * @param   serviceId
     *          The specific service id
     * @param   <T>
     *          The service type
     * @return  The specific service instance or null
     */
    <T> T findService(String serviceId);

    /**
     * The IInjectable should be replaced by IService1 interface
     * @param service
     */
    void register(uapi.IService service);

    /**
     * The method is used for registering service which is from outside of framework
     * @param serviceId
     * @param service
     */
    void register(String serviceId, Object service);
}
