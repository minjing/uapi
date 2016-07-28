/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
