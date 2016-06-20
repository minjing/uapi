/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote;

import uapi.IIdentifiable;
import uapi.service.ServiceMeta;

/**
 * The service invocation driver is used to invoke service
 */
public interface ICommunicator extends IIdentifiable<String> {

    Object request(ServiceMeta serviceMeta, Object... args);
}
