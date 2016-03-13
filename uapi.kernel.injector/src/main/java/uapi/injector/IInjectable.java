package uapi.injector;

import uapi.InvalidArgumentException;
import uapi.KernelException;

/**
 * Implement this interface will indicate the object can be injected by
 * specific service instance.
 */
public interface IInjectable {

    /**
     * Inject an object to this service.
     *
     * @param   injection
     *          The injection contain the meta information about injected object to this service
     * @throws  InvalidArgumentException
     *          The injection is null
     * @throws  KernelException
     *          The object can't be injected to this service
     */
    void injectObject(
            final Injection injection
    ) throws InvalidArgumentException, KernelException;

    /**
     * Indicate specified service id is optional depends on or not
     *
     * @param   id
     *          The service id which will be checked
     * @return  Return true if the service is optional depends on, otherwise return false
     * @throws  InvalidArgumentException
     *          If the specified id is null
     */
    boolean isOptional(final String id) throws InvalidArgumentException;
}
