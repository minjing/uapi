package uapi.state;

import uapi.state.internal.State;

/**
 * The utility class is used to create state instance
 */
public class StateCreator {

    public static <T> IState<T> createState(IShifter<T> shifter, T initState) {
        return new State<>(shifter, initState);
    }

    private StateCreator() { }
}
