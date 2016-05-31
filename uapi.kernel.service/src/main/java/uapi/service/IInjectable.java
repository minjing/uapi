/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.InvalidArgumentException;
import uapi.KernelException;

/**
 * Implement this interface will indicate the object can be injected by
 * specific service instance.
 */
public interface IInjectable {

    /**
     * Inject an object to this service.
     *
     * @param   injection
     *          The injection contain the meta information about injected object to this service
     * @throws  InvalidArgumentException
     *          The injection is null
     * @throws  KernelException
     *          The object can't be injected to this service
     */
    void injectObject(
            final Injection injection
    ) throws InvalidArgumentException, KernelException;

    /**
     * Return service ids which is this service depends on
     *
     * @return  Dependent service id array
     */
    String[] getDependentIds();

    /**
     * Indicate specified service id is optional depends on or not
     *
     * @param   id
     *          The service id which will be checked
     * @return  Return true if the service is optional depends on, otherwise return false
     * @throws  InvalidArgumentException
     *          If the specified id is null
     */
    boolean isOptional(final String id) throws InvalidArgumentException;
}
