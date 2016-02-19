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
     *          The object is null
     * @throws  KernelException
     *          The object can't be injected to this service
     */
    void injectObject(
            final Injection injection
    ) throws InvalidArgumentException, KernelException;
}
