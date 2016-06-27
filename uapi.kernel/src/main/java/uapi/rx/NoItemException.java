package uapi.rx;

import uapi.KernelException;

/**
 * The exception indicate no item can be generated
 */
public class NoItemException extends KernelException {

    public NoItemException() {
        super("There is no item from previously operator");
    }
}
