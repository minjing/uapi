package uapi.app;

/**
 * The IAppLifecycle contains some callback methods which will be invoked by application lifecycle
 */
public interface IAppLifecycle {

    /**
     * Invoked when application is started
     */
    void onStarted();

    /**
     * Invoked when application is stopped
     */
    void onStopped();
}
