package uapi;

import uapi.helper.StringHelper;

public class KernelException extends RuntimeException {

    private static final long serialVersionUID = -3398540245462767129L;

    private final String    _msg;
    private final Object[]  _args;
    
    public KernelException(String message, Object... arguments) {
        this._msg = message;
        this._args = arguments;
    }

    public KernelException(Throwable t, String message, Object... arguments) {
        super(t);
        this._msg = message;
        this._args = arguments;
    }

    @Override
    public String getMessage() {
        return StringHelper.makeString(this._msg, this._args);
    }
}
