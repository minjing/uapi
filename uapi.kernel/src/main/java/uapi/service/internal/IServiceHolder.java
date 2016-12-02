package uapi.service.internal;

/**
 * A service holder
 */
public interface IServiceHolder {

    /**
     * Try to activate the service
     *
     * @return  True means the service is activated otherwise it is failed
     */
    boolean tryActivate();

    /**
     * Try to activate the service
     *
     * @param   throwException
     *          Throw exception when the service can't be resolved, injected, satisfied...
     * @return  True means the service is activated otherwise it is failed
     */
    boolean tryActivate(boolean throwException);
}
