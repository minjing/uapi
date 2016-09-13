/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import uapi.service.IService;

/**
 * A profile to control which service is allowed to load into application
 */
interface IProfile {

    /**
     * Check specified service can be loaded to application
     *
     * @param   service
     *          The service which need to be check
     * @return  True means the service is allowed otherwise denied
     */
    boolean isAllow(IService service);
}
