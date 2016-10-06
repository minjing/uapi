package uapi.event;

import uapi.KernelException;

/**
 * The exception will be thrown when no handler can handle specific event
 */
public class NoEventHandlerException extends KernelException {

    /**
     * Construct NoEventHandlerException instance
     *
     * @param   topic
     *          Event topic
     */
    public NoEventHandlerException(String topic) {
        super("There are no event handler for event topic - {}", topic);
    }
}
