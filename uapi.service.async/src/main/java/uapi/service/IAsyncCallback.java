/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

/**
 * The call back for async service invocation
 */
public interface IAsyncCallback {

    /**
     * Invoke this method before wrapped service call, this purpose of this method is provide the callId
     * of the call.
     *
     * @param   callId
     *          The call id
     * @param   methodName
     *          The name of method which will be called soon
     * @param   arguments
     *          The arguments of called method
     */
    void calling(String callId, String methodName, Object[] arguments);

    /**
     * Invoke this method after call successful
     *
     * @param   callId
     *          The call id
     * @param   result
     *          The result object of the call
     */
    void succeed(String callId, Object result);

    /**
     * Invoke this method after call failed
     *
     * @param   callId
     *          The call id
     * @param   t
     *          The throwable exception
     */
    void failed(String callId, Throwable t);

    /**
     * Invoke this method after the call is timed out
     *
     * @param   callId
     *          The call id
     */
    void timedout(String callId);
}
