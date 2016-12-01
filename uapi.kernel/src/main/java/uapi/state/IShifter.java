package uapi.state;

/**
 * A shifter is used to change state based on specific operation
 */
@FunctionalInterface
public interface IShifter<T> {

    /**
     * Apply operation and may change current state
     *
     * @param   state
     *          The current state
     * @param   operation
     *          The operation
     * @return  The new state
     */
    T shift(T state, IOperation operation);
}
