package uapi.state;

/**
 * The state hold state data and logic which used to change state based on specific operation
 */
public interface IState<T> {

    /**
     * Subscribe this state on specific listener
     *
     * @param   listener
     *          The listener which will be notified when state changes
     */
    void subscribe(IStateListener<T> listener);

    /**
     * Unsubscribe this state on specific listener
     *
     * @param   listener
     *          The listener
     */
    void unsubscribe(IStateListener<T> listener);

    /**
     * Receive current state
     *
     * @return  The current state
     */
    T get();

    /**
     * Apply operation on this state which may change current state
     *
     * @param   operation
     *          The operation
     */
    void shift(IOperation operation);

    /**
     * Apply operation which has no attached data on this state
     *
     * @param   operationType
     *          The operation type
     */
    void shift(String operationType);
}
