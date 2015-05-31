package uapi;

public class InvalidArgumentException extends KernelException {

    private static final long serialVersionUID = -1108366334118915560L;

    public InvalidArgumentException(String argumentName, InvalidArgumentType type) {
        super("The argument is invalid - {}, cause - {}", argumentName, type.name());
    }

    public static enum InvalidArgumentType {

        EMPTY, FORMAT
    }
}
