/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal;

import com.fasterxml.jackson.jr.ob.JSON;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.web.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IHttpRequest} by Netty
 */
class NettyHttpRequest implements IHttpRequest {

    private final HttpMessage _msg;
    private final Map<String, String> _headers = new HashMap<>();
    private final Map<String, List<String>> _params = new HashMap<>();
    private String _uri;
    private Object _jsonObj;
    private uapi.web.http.HttpMethod _method;
    private uapi.web.http.HttpVersion _version;

    NettyHttpRequest(final HttpMessage msg) {
        if (msg == null) {
            throw new InvalidArgumentException("msg", InvalidArgumentException.InvalidArgumentType.EMPTY);
        }
        this._msg = msg;
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            HttpHeaders headers = request.headers();
            Looper.from(headers.iteratorAsString())
                    .foreach(entry -> this._headers.put(entry.getKey().toLowerCase(), entry.getValue()));

            this._uri = request.uri();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(this._uri);
            Map<String, List<String>> params = queryStringDecoder.parameters();
            Looper.from(params.entrySet())
                    .foreach(entry -> this._params.put(entry.getKey(), entry.getValue()));

            HttpVersion version = request.protocolVersion();
            if (HttpVersion.HTTP_1_0.equals(version)) {
                this._version = uapi.web.http.HttpVersion.V_1_0;
            } else if (HttpVersion.HTTP_1_1.equals(version)) {
                this._version = uapi.web.http.HttpVersion.V_1_1;
            } else {
                throw new KernelException("Unsupported Http version - {}", version);
            }

            HttpMethod method = request.method();
            if (HttpMethod.GET.equals(method)) {
                this._method = uapi.web.http.HttpMethod.GET;
            } else if (HttpMethod.PUT.equals(method)) {
                this._method = uapi.web.http.HttpMethod.PUT;
            } else if (HttpMethod.POST.equals(method)) {
                this._method = uapi.web.http.HttpMethod.POST;
            } else if (HttpMethod.DELETE.equals(method)) {
                this._method = uapi.web.http.HttpMethod.DELETE;
            } else {
                throw new KernelException("Unsupported http method {}", method.toString());
            }

            HttpMethod httpMethod = request.method();
            if (httpMethod.equals(HttpMethod.POST)) {
                String contentTypeStr = this._headers.get(HttpHeaderNames.CONTENT_TYPE.toString());
                if (ArgumentChecker.isEmpty(contentTypeStr)) {
                    throw new KernelException("The {} must be specified in POST request", HttpHeaderNames.CONTENT_TYPE.toString());
                }
                String[] contentTypes = this._headers.get(HttpHeaderNames.CONTENT_TYPE.toString()).split(";");
                String contentType;
                if (contentTypes.length < 0) {
                    throw new KernelException("No content type was specific");
                } else {
                    contentType = contentTypes[0];
                }
                if (contentType.equals("application/json")) {
                    FullHttpRequest fullHttpRequest = (FullHttpRequest) request;
                    String jsonStr = fullHttpRequest.content().toString();
                    try {
                        this._jsonObj = JSON.std.anyFrom(jsonStr);
                    } catch (IOException ex) {
                        throw new KernelException(ex);
                    }
                } else if (contentType.equals("application/x-www-form-urlencoded")) {
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
                    List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
                    Looper.from(datas)
                            .filter(data -> data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute)
                            .map(data -> (Attribute) data)
                            .foreach(attr -> {
                                List<String> values = this._params.get(attr.getName());
                                if (values == null) {
                                    values = new ArrayList<>();
                                    this._params.put(attr.getName(), values);
                                }
                                try {
                                    values.add(attr.getValue());
                                } catch (IOException ex) {
                                    throw new KernelException(ex);
                                }
                            });
                } else {
                    throw new KernelException("Unsupported content type {}", contentType);
                }
            }
        }
    }

    @Override
    public boolean isKeepAlive() {
        return HttpUtil.isKeepAlive(this._msg);
    }

    @Override
    public String uri() {
        return this._uri;
    }

    @Override
    public uapi.web.http.HttpVersion version() {
        return this._version;
    }

    @Override
    public uapi.web.http.HttpMethod method() {
        return this._method;
    }

    public Object jsonObject() {
        return this._jsonObj;
    }

    @Override
    public Map<String, String> headers() {
        return this._headers;
    }

    @Override
    public Map<String, List<String>> params() {
        return this._params;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("URI: ").append(this._uri).append("\n");
        buffer.append("METHOD: ").append(this._method).append("\n");
        buffer.append("HEADERS: \n");
        Looper.from(this._headers.entrySet())
                .map(entry -> StringHelper.makeString("\t{} = {}\n", entry.getKey(), entry.getValue()))
                .foreach(buffer::append);
        buffer.append("PARAMETERS: \n");
        Looper.from(this._params.entrySet())
                .map(entry -> StringHelper.makeString("\t{} = {}\n", entry.getKey(), entry.getValue()))
                .foreach(buffer::append);
        if (this._jsonObj != null) {
            buffer.append("JSON OBJECT:\n").append("\t").append(this._jsonObj.toString()).append("\n");
        }
        return buffer.toString();
    }
}
