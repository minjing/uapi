/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.service.internal.QualifiedServiceId;

/**
 * A service reference hold a service and provide only some limited functionality
 */
public interface IServiceReference {

    /**
     * Retrieve the service id
     *
     * @return  Service id
     */
    String getId();

    /**
     * Retrieve the service qualified id
     * The qualified id is composed with service id and service form
     *
     * @return  The service qualified id
     */
    QualifiedServiceId getQualifiedId();

    /**
     * Retrieve where is the service from
     *
     * @return  Where is the service from
     */
    String getFrom();

    /**
     * Retrieve the service instance
     *
     * @return  The service instance
     */
    Object getService();

    /**
     * When the service is satisfied, the method wil be invoked
     */
    void notifySatisfied();
}
