/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

/**
 * A service loader used to load external service
 */
public interface IServiceLoader {

    /**
     * The name of service loader
     *
     * @return  The service name
     */
    String getName();

    /**
     * Load service by id and type
     *
     * @param   serviceId
     *          The service id
     * @param   serviceType
     *          The service type
     * @param   <T>
     *          The service instance type
     * @return  The service instance or null
     */
    <T> T load(final String serviceId, final Class<?> serviceType);
}
