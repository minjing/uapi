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

/**
 * The IServiceInterfaceProxy will act as specific service proxy to
 * interactive with remote real service.
 */
public interface IServiceInterfaceProxy {

    /**
     * Get a communicator which used for interaction
     */
    ICommunicator getCommunicator();

    /**
     * Get service interface meta class
     */
    ServiceInterfaceMeta getMeta();
}
