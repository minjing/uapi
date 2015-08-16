package uapi;

/**
 * The watcher watch object state change event
 * 
 * @author min
 */
public interface IStateWatcher {

    /**
     * Invoked when the object state was changed
     * 
     * @param   which
     *          The object which state was changed
     * @param   oldState
     *          The old state
     * @param   newState
     *          The new state
     */
    void stateChanged(IStateful which, int oldState, int newState);

    /**
     * Invoked when the object state was changed
     * 
     * @param   which
     *          The object which state was changed
     * @param   oldState
     *          The old state
     * @param   newState
     *          The new state
     * @param   t
     *          raised exception during state change
     */
    void stateChange(IStateful which, int oldState, int newState, Throwable t);
}
