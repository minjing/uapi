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
 * Created by xquan on 9/20/2016.
 */
public interface IAsyncService {

    String OPTION_TIME_OUT  = "TimeOut";

    /**
     * Run statement and ignore it's result and don't care it failed or not.
     *
     * @param   runnable
     *          A runnable statements
     * @return  Call id
     */
    String call(Runnable runnable);

    String call(Runnable runnable, Map<String, Object> options);

    String call(Callable callable, ICallSucceed succeedCallback);

    String call(Callable callable, ICallSucceed succeedCallback, Map<String, Object> options);

    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback);

    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback, Map<String, Object> options);

    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback, ICallTimedOut timedOutCallback);

    String call(Callable callable, ICallSucceed succeedCallback, ICallFailed failedCallback, ICallTimedOut timedOutCallback, Map<String, Object> options);
}
