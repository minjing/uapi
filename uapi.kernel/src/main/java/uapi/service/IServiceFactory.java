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
 * A service factory which used for service creation
 * Normally service is singleton, but for such case that each service reference need a new
 * service, we can uses IServiceFactory.
 *
 * @param   <T>
 *          The type of service which is created by the service factory
 */
public interface IServiceFactory<T> {

    /**
     * Create new service instance
     *
     * @param   serveFor
     *          Indicate which object uses the new created service
     * @return  The new service instance
     */
    T createService(Object serveFor);
}
