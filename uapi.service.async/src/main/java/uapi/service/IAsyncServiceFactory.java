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
 * The service used to create async builder
 */
public interface IAsyncServiceFactory {

    /**
     * Create new builder for IAsyncService
     *
     * @param   serviceType
     *          The service type which is delegated by new async service
     * @param   <T>
     *          The delegated service type
     * @return  The builder for async service
     */
    <T extends IService> IAsyncServiceBuilder<T> newBuilder(Class<T> serviceType);
}
