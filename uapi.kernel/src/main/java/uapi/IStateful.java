package uapi;

/**
 * Represent an object with specific state
 * 
 * @author min
 */
public interface IStateful {

    /**
     * Initial state
     */
    int STATE_INIT      = 0;

    /**
     * Terminal state
     */
    int STATE_TERMINAL  = 128;

    /**
     * Set the state change watcher
     * 
     * @param   watcher
     *          The state change watcher
     */
    void setWatcher(IStateWatcher watcher);

    /**
     * Get the state change watcher
     * 
     * @return  The state change watcher
     */
    IStateWatcher getWatcher();
}
