package uapi.service;

/**
 * A watcher used to watch service status
 */
public interface IWatcher {

    /**
     * Invoked when a service is added to registry
     *
     * @param   serviceRef
     *          The new added service reference
     */
    void onRegister(IServiceReference serviceRef);

    /**
     * Invoked when a service is resolved
     *
     * @param   serviceRef
     *          The resolved service reference
     */
    void onResolved(IServiceReference serviceRef);
}
