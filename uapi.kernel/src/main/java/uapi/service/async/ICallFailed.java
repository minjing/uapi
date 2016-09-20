/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.async;

/**
 * Created by xquan on 9/20/2016.
 */
public interface ICallFailed {

    /**
     * Invoke this method after call failed
     *
     * @param   callId
     *          The call id
     * @param   t
     *          The throwable exception
     */
    void accept(String callId, Throwable t);
}
