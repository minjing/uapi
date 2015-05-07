package uapi.kernel.internal;

public enum ServiceState {

    /**
     * The service has been resolved
     * but all dependencies is not checked.
     */
    RESOLVED,

    /**
     * All dependencies has been set to the service
     */
    SATISFIED,

    /**
     * The service has been initialized which mean
     * the initial method has been invoked if it has
     */
    INITIALIZED
}
