/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.log.internal;

import uapi.helper.StringHelper;
import uapi.log.ILogger;

final class Logger implements ILogger {

    private final org.slf4j.Logger _slfLogger;

    Logger(org.slf4j.Logger slfLogger) {
        this._slfLogger = slfLogger;
    }

    @Override
    public void trace(String message, Object... parameters) {
        this._slfLogger.trace(message, parameters);
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
    public void warn(Throwable t) {
        this._slfLogger.warn(t.getMessage(), t);
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
    public void error(Throwable t) {
        this._slfLogger.error(t.getMessage(), t);
    }

    @Override
    public void error(Throwable t, String message, Object... parameters) {
        this._slfLogger.error(StringHelper.makeString(message, parameters), t);
    }
}
