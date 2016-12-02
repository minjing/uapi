package uapi.state;

import uapi.state.internal.StateTracer;

/**
 * The utility class is used to create state instance
 */
public class StateCreator {

    public static <T> IStateTracer<T> createTracer(IShifter<T> shifter, T initState) {
        return new StateTracer<>(shifter, initState);
    }

    private StateCreator() { }
}
