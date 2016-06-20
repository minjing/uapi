/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal;

import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.service.ServiceMeta;
import uapi.service.web.ArgumentMapping;
import uapi.service.web.HttpMethod;

import java.util.List;

/**
 * Restful service meta
 */
public class RestfulServiceMeta extends ServiceMeta {

    private String _uri;
    private HttpMethod[] _methods;
    private String _codec;

    public RestfulServiceMeta(
            final String name,
            final String returnTypeName,
            final List<ArgumentMapping> argMappings,
            final String uri,
            final HttpMethod[] httpMethods,
            final String codec
    ) {
        super(name, returnTypeName, argMappings);
        ArgumentChecker.required(uri, "uri");
        ArgumentChecker.required(httpMethods, "httpMethods");
        ArgumentChecker.required(codec, "codec");
        this._uri = uri;
        this._methods = httpMethods;
        this._codec = codec;
    }

    public String getUri() {
        return this._uri;
    }

    public HttpMethod[] getMethods() {
        return this._methods;
    }

    public String getCodec() {
        return this._codec;
    }

    public String getCommunicatorName() {
        return RestfulCommunicator.id;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("RestfulServiceMeta[{},uri={},methods={},codec{}]",
                super.toString(), this._uri, CollectionHelper.asString(this._methods), this._codec);
    }
}
