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
 * A ServiceListener listen service related event from framework
 */
public interface IServiceListener {

    /**
     * Invoke when an optional dependency is satisfied.
     *
     * @param   serviceId
     *          The dependent service id
     * @param   object
     *          The dependent service instance
     */
    void onDependencySet(String serviceId, Object object);
}
