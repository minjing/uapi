package uapi.kernel;

public class InvalidStateException extends KernelException {

    private static final long serialVersionUID = -1108366334118915560L;

    public InvalidStateException(String currentState, String expectedState) {
        super("Crrent state is {}, Expected state is {}", currentState, expectedState);
    }
}
