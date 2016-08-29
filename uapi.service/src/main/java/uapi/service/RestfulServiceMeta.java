/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.web.http.HttpMethod;

import java.util.List;

/**
 * Restful service meta
 */
public class RestfulServiceMeta extends ServiceMeta {

    private final String _host;
    private final int _port;
    private final String _ctx;
    private final String _uri;
    private final List<HttpMethod> _methods;
    private final String _codec;

    public RestfulServiceMeta(
            final String host,
            final int port,
            final String name,
            final String returnTypeName,
            final List<ArgumentMeta> argMappings,
            final String context,
            final List<HttpMethod> httpMethods,
            final String codec
    ) {
        super(name, returnTypeName, argMappings);
        ArgumentChecker.required(host, "host");
        ArgumentChecker.required(context, "context");
        ArgumentChecker.required(httpMethods, "httpMethods");
        ArgumentChecker.required(codec, "codec");
        if (port <= 0) {
            throw new InvalidArgumentException("Invalid port argument - {}", port);
        }
        this._host = host;
        this._port = port;
        this._ctx = context;
        this._uri = StringHelper.makeString("http://{}:{}{}", host, port, context);
        this._methods = httpMethods;
        this._codec = codec;
    }

    public String getHost() {
        return this._host;
    }

    public int getPort() {
        return this._port;
    }

    public String getContext() {
        return this._ctx;
    }

    public String getUri() {
        if (getId() == null) {
            return this._uri;
        } else {
            return this._uri + "/" + getId();
        }
    }

    public List<HttpMethod> getMethods() {
        return this._methods;
    }

    public String getCodec() {
        return this._codec;
    }

//    public String getCommunicatorName() {
//        return RestfulCommunicator.id;
//    }

    @Override
    public String toString() {
        return StringHelper.makeString("RestfulServiceMeta[{},uri={},methods={},codec{}]",
                super.toString(), this._uri, CollectionHelper.asString(this._methods), this._codec);
    }
}
