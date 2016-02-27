package uapi.service;

import uapi.InvalidArgumentException;

/**
 * A registry for storing service, it has following features:
 *  * Hold service in local map
 *  * Resolve dependency between services
 */
public interface IRegistry {

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
}
