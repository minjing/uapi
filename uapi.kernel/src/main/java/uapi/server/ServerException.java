package uapi.server;

import uapi.KernelException;

/**
 * The exception will be thrown when server encounter an exception
 */
public class ServerException extends KernelException {

    public ServerException(String message, Object... arguments) {
        super(message, arguments);
    }

    public ServerException(Throwable t) {
        super(t);
    }

    public ServerException(Throwable t, String message, Object... arguments) {
        super(t, message, arguments);
    }
}
