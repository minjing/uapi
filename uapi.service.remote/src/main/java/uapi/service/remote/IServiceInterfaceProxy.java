/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote;

import uapi.service.ServiceInterfaceMeta;

import java.util.List;

/**
 * The IServiceInterfaceProxy will act as specific service proxy to
 * interactive with remote real service.
 */
public interface IServiceInterfaceProxy {

    /**
     * Set a communicator which used for interaction
     *
     * @param   communicator
     *          The ICommunicator instance
     */
    void setCommunicators(List<ICommunicator> communicator);

    /**
     * Set service interface meta class
     *
     * @param   meta
     *          Service interface meta
     */
    void setMeta(ServiceInterfaceMeta meta);
}
