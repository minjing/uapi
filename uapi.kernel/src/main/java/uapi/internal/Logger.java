package uapi.internal;

import uapi.helper.StringHelper;
import uapi.log.ILogger;

final class Logger implements ILogger {

    private final org.slf4j.Logger _slfLogger;
    
    Logger(org.slf4j.Logger slfLogger) {
        this._slfLogger = slfLogger;
    }

    @Override
    public void debug(String message, Object... parameters) {
        this._slfLogger.debug(message, parameters);
    }

    @Override
    public void info(String message, Object... parameters) {
        this._slfLogger.info(message, parameters);
    }

    @Override
    public void warn(String message, Object... parameters) {
        this._slfLogger.warn(message, parameters);
    }

    @Override
    public void warn(Throwable t, String message, Object... parameters) {
        this._slfLogger.warn(StringHelper.makeString(message, parameters), t);
    }

    @Override
    public void error(String message, Object... parameters) {
        this._slfLogger.error(message, parameters);
    }

    @Override
    public void error(Throwable t, String message, Object... parameters) {
        this._slfLogger.error(StringHelper.makeString(message, parameters), t);
    }

}
