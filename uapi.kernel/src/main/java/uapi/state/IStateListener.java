package uapi.state;

/**
 * The listener used to monitor specific state
 */
public interface IStateListener<T> {

    /**
     * Invoked when state changed
     *
     * @param   oldState
     *          The old state
     * @param   newState
     *          The new state
     */
    void stateChanged(T oldState, T newState);
}
