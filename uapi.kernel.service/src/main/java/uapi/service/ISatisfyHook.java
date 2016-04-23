package uapi.service;

/**
 * When a service is ready means the all this service dependencies is injected
 * but before initialize the service we must check another conditions are satisfied
 * for example the service depends on someone configuration.
 * Use ISatisfyHook to ensure the service is satisfied before initialize the service
 */
public interface ISatisfyHook {

    /**
     * Ensure the service is satisfied
     *
     * @param   serviceReference
     *          The service reference which will be evaluated
     * @return  true means the service is satisfied otherwise will return false
     */
    boolean isSatisfied(IServiceReference serviceReference);
}
