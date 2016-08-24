/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful;

import uapi.service.ServiceMeta;
import uapi.web.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * A Restful interface combine multiple Restful service into a interface to exposed
 */
public interface IRestfulInterface {

    /**
     * Retrieve interface id
     *
     * @return  The interface id
     */
    String getInterfaceId();

    /**
     * Retrieve service and http method mapping info
     *
     * @return  mapping info
     */
    Map<ServiceMeta, List<HttpMethod>> getMethodHttpMethodInfos();
}
