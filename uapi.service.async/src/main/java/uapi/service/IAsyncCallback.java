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
 * The call back
 */
public interface IAsyncCallback {

    void calling(String callId, String methodName, Object[] arguments);

    void succeed(String callId, Object result);

    void failed(String callId, Throwable t);

    void timedout(String callId);
}
