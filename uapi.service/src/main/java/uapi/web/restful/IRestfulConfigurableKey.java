/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful;

import uapi.web.http.IHttpConfigurableKey;

/**
 * Created by xquan on 8/24/2016.
 */
public interface IRestfulConfigurableKey extends IHttpConfigurableKey {

    String RESTFUL_URI_PREFIX       = "restful.uri-prefix";
    String RESTFUL_CODEC            = "restful.codec";
}
