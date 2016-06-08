/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web;

import uapi.config.IConfigurableKey;

/**
 * The interface hold all configurable keys for web
 */
public interface IWebConfigurableKey extends IConfigurableKey {

    /**
     * Below configurations are used in JetterHttpServer
     */
    String SERVER_HTTP_HOST         = "server.http.host";
    String SERVER_HTTP_PORT         = "server.http.port";
    String SERVER_HTTP_IDLE_TIMEOUT = "server.http.idle-timeout";

    /**
     * Below configurations are used in RestfulServiceServlet
     */
    String RESTFUL_URI_PREFIX       = "restful.uri-prefix";
    String RESTFUL_CODEC            = "restful.codec";
}
