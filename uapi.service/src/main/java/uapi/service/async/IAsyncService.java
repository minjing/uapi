/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.async;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * The interface is used for wrapping a synchronous statement to provide asynchronous call functionality.
 */
public interface IAsyncService {

    String OPTION_TIME_OUT  = "TimeOut";

    /**
     * Run specified statement and ignore it's result and don't care it failed or not.
     *
     * @param   runnable
     *          A runnable statement
     * @return  Call id
     */
    String call(Runnable runnable);

    /**
     * Run specified statement with specified options and ignore the statement result if it has.
     *
     * @param   runnable
     *          A runnable statement
     * @param   options
     *          Call options
     * @return  Call id
     */
    String call(Runnable runnable, Map<String, Object> options);

    /**
     * Call specified statement with specified success call back to receive call result.
     *
     * @param   callable
     *          A runnable statement
     * @param   succeedCallback
     *          Invoked when the call succeed and return the result
     * @return  Call id
     */
    String call(Callable callable, ICallSucceed succeedCallback);

    /**
     * Call specified statement which succeed call back and call options.
     *
     * @param   callable
     *          A runnable statement
     * @param   succeedCallback
     *          Invoked when the call succeed and return the result
     * @param   options
     *          The call options
     * @return  Call id
     */
    String call(Callable callable, ICallSucceed succeedCallback, Map<String, Object> options);

    /**
     * Call specified statement which succeed call back and failed call back.
     *
     * @param   callable
     *          A runnable statement
     * @param   succeedCallback
     *          Invoked when the call succeed and return the result
     * @param   failedCallback
     *          Invoked when the call failed and return the exception if it has
     * @return  Call id
     */
    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback);

    /**
     * Call specified statement which succeed call back and failed call back.
     *
     * @param   callable
     *          A runnable statement
     * @param   succeedCallback
     *          Invoked when the call succeed and return the result
     * @param   failedCallback
     *          Invoked when the call failed and return the exception if it has
     * @param   options
     *          The call options
     * @return  Call id
     */
    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback, Map<String, Object> options);

    /**
     * Call specified statement which succeed call back, failed call back and time out call back.
     *
     * @param   callable
     *          A runnable statement
     * @param   succeedCallback
     *          Invoked when the call succeed and return the result
     * @param   failedCallback
     *          Invoked when the call failed and return the exception if it has
     * @param   timedOutCallback
     *          Invoked when the call timed out
     * @return  Call id
     */
    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback, ICallTimedOut timedOutCallback);

    /**
     * Call specified statement which succeed call back, failed call back and time out call back.
     *
     * @param   callable
     *          A runnable statement
     * @param   succeedCallback
     *          Invoked when the call succeed and return the result
     * @param   failedCallback
     *          Invoked when the call failed and return the exception if it has
     * @param   timedOutCallback
     *          Invoked when the call timed out
     * @param   options
     *          The call options
     * @return  Call id
     */
    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback, ICallTimedOut timedOutCallback, Map<String, Object> options);
}
