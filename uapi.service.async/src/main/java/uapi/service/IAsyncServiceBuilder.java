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
 * The builder for async service
 */
public interface IAsyncServiceBuilder<T extends IService> {

    /**
     * Indicate which service is delegated by async service
     *
     * @param   service
     *          The service instance
     * @return  The async service builder
     */
    IAsyncServiceBuilder<T> on(T service);

    /**
     * Indicate the callback instance which used in async service invocation life cycle.
     *
     * @param   callback
     *          The async service call back instance
     * @return  The async service builder
     */
    IAsyncServiceBuilder<T> with(IAsyncCallback callback);

    /**
     * Set the timeout time of the async service invocation
     *
     * @param   time
     *          The timeout time, if less than 1 which means no time out
     * @return  The async service builder
     */
    IAsyncServiceBuilder<T> timeout(int time);

    /**
     * Build a async service for specific service
     *
     * @return  Async service instance
     */
    T build();
}
