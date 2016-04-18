package uapi.service;

import uapi.InvalidArgumentException;

import java.util.List;

/**
 * A registry for storing service, it has following features:
 *  * Hold service in local map
 *  * Resolve dependency between services
 */
public interface IRegistry extends IInitial {

    /**
     * Register a service
     *
     * @param   service
     *          The service which will be registered
     * @throws  InvalidArgumentException
     *          The exception will be thrown when the service is null
     */
    void register(
            final IService service
    ) throws InvalidArgumentException;

    /**
     * Register more service
     *
     * @param   services
     *          The services which will be registered
     * @throws  InvalidArgumentException
     *          The exception will be thrown when the service is null
     */
    void register(
            final IService... services
    ) throws InvalidArgumentException;

    /**
     * Register a generic object as a service
     *
     * @param   service
     *          The service object
     * @param   serviceIds
     *          The related identifies of the service object
     * @throws  InvalidArgumentException
     *          If the service is null or the related identifies is not specified
     */
    void register(
            final Object service,
            String... serviceIds
    ) throws InvalidArgumentException;

    /**
     * Find service by specified service id
     *
     * @param   serviceId
     *          The specified service id
     * @param   <T>
     *          The service type
     * @return  The service instance
     */
    <T> T findService(final String serviceId);

    /**
     * Find service by specific service type
     *
     * @param   serviceType
     *          The specified service type
     * @param   <T>
     *          The service type
     * @return  The service instance
     */
    <T> T findService(final Class<T> serviceType);

    /**
     * Find multiple service by specific service id
     *
     * @param   serviceId
     *          The service id which used for service finding
     * @param   <T>
     *          The service type
     * @return  The service list
     */
    <T> List<T> findServices(final String serviceId);

    /**
     * Find multiple services by specific service type
     *
     * @param   serviceType
     *          The service type which used for service finding
     * @param   <T>
     *          The service type
     * @return  The service list
     */
    <T> List<T> findServices(final Class<T> serviceType);
}
