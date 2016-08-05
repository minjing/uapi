/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http;

import uapi.config.IConfigurableKey;

/**
 * Created by xquan on 8/5/2016.
 */
public interface IHttpConfigurableKey extends IConfigurableKey {

    /**
     * Below configurations are used in JetterHttpServer
     */
    String SERVER_HTTP_HOST         = "server.http.host";
    String SERVER_HTTP_PORT         = "server.http.port";
}
