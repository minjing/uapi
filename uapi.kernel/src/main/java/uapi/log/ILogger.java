/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.log;

public interface ILogger {

    void trace(String message, Object... parameters);

    void debug(String message, Object... parameters);

    void info(String message, Object... parameters);

    void warn(String message, Object... parameters);

    void warn(Throwable t);

    void warn(Throwable t, String message, Object... parameters);

    void error(String message, Object... parameters);

    void error(Throwable t);

    void error(Throwable t, String message, Object... parameters);
}
