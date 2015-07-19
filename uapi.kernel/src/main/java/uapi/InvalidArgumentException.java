package uapi;

/**
 * The exception indcate that an invalid argument was found in system.
 */
public class InvalidArgumentException extends KernelException {

    private static final long serialVersionUID = -1108366334118915560L;

    public InvalidArgumentException(String argumentName, InvalidArgumentType type) {
        this("The argument is invalid - {}, cause - {}", argumentName, type.name());
    }

    public InvalidArgumentException(String message, Object... args) {
        super(message, args);
    }

    public static enum InvalidArgumentType {

        EMPTY, FORMAT
    }
}
