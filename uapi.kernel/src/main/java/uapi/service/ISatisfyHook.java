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
 * When a service is ready means the all this service dependencies is injected
 * but before initialize the service we must check another conditions are satisfied
 * for example the service depends on someone configuration.
 * Use ISatisfyHook to ensure the service is satisfied before initialize the service
 */
public interface ISatisfyHook {

    /**
     * Ensure the service is satisfied
     *
     * @param   serviceReference
     *          The service reference which will be evaluated
     * @return  true means the service is satisfied otherwise will return false
     */
    boolean isSatisfied(IServiceReference serviceReference);
}
