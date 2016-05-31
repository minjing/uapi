/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi;

import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

/**
 * Basic exception, all exception should extend it
 */
public class KernelException extends RuntimeException {

    private static final long serialVersionUID = -3398540245462767129L;

    private final String    _msg;
    private final Object[]  _args;
    
    public KernelException(String message, Object... arguments) {
        this._msg = message;
        this._args = arguments;
    }

    public KernelException(Throwable t) {
        super(t);
        this._msg = t.getMessage();
        this._args = CollectionHelper.emptyArray;
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
