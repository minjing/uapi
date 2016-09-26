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
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import uapi.KernelException;
import uapi.helper.StringHelper;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.web.http.ContentType;
import uapi.web.http.IHttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IHttpRequest} by Netty
 */
class NettyHttpRequest implements IHttpRequest {

    private final ILogger _logger;

    private final HttpRequest _request;
    private final Map<String, String> _headers = new HashMap<>();
    private final Map<String, List<String>> _trailers = new HashMap<>();
    private final Map<String, List<String>> _params = new HashMap<>();
    private Object _objParam;
    private String _textParam;
    private String _uri;
    private uapi.web.http.HttpMethod _method;
    private uapi.web.http.HttpVersion _version;
    private List<ByteBuf> _bodyParts = new ArrayList<>();
    private boolean _lastBodyPart = true;

    private final ContentType _conentType;
    private final Charset _charset;

    NettyHttpRequest(final ILogger logger, final HttpRequest httpRequest) {
        this._logger = logger;
        this._request = httpRequest;

        HttpHeaders headers = this._request.headers();
        Looper.from(headers.iteratorAsString())
                .foreach(entry -> this._headers.put(entry.getKey().toLowerCase(), entry.getValue()));

        this._uri = this._request.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(this._uri);
        Map<String, List<String>> params = queryStringDecoder.parameters();
        Looper.from(params.entrySet())
                .foreach(entry -> this._params.put(entry.getKey(), entry.getValue()));

        HttpVersion version = this._request.protocolVersion();
        if (HttpVersion.HTTP_1_0.equals(version)) {
            this._version = uapi.web.http.HttpVersion.V_1_0;
        } else if (HttpVersion.HTTP_1_1.equals(version)) {
            this._version = uapi.web.http.HttpVersion.V_1_1;
        } else {
            throw new KernelException("Unsupported Http version - {}", version);
        }

        HttpMethod method = this._request.method();
        if (HttpMethod.GET.equals(method)) {
            this._method = uapi.web.http.HttpMethod.GET;
        } else if (HttpMethod.PUT.equals(method)) {
            this._method = uapi.web.http.HttpMethod.PUT;
        } else if (HttpMethod.POST.equals(method)) {
            this._method = uapi.web.http.HttpMethod.POST;
        } else if (HttpMethod.PATCH.equals(method)) {
            this._method = uapi.web.http.HttpMethod.PATCH;
        } else if (HttpMethod.DELETE.equals(method)) {
            this._method = uapi.web.http.HttpMethod.DELETE;
        } else {
            throw new KernelException("Unsupported http method {}", method.toString());
        }

        // Decode content type
        String contentTypeString = this._headers.get(HttpHeaderNames.CONTENT_TYPE.toString());
        if (contentTypeString == null) {
            this._conentType = ContentType.TEXT;
            this._charset = Charset.forName("UTF-8");
        } else {
            String[] contentTypeInfo = contentTypeString.split(";");
            if (contentTypeInfo.length < 0) {
                this._conentType = ContentType.TEXT;
                this._charset = CharsetUtil.UTF_8;
            } else if (contentTypeInfo.length == 1) {
                this._conentType = ContentType.parse(contentTypeInfo[0].trim());
                this._charset = CharsetUtil.UTF_8;
            } else {
                this._conentType = ContentType.parse(contentTypeInfo[0].trim());
                this._charset = Looper.from(contentTypeInfo)
                        .map(info -> info.split("="))
                        .filter(kv -> kv.length == 2)
                        .filter(kv -> kv[0].trim().equalsIgnoreCase("charset"))
                        .map(kv -> kv[1].trim())
                        .map(Charset::forName)
                        .first(CharsetUtil.UTF_8);
            }
        }
    }

    @Override
    public boolean isKeepAlive() {
        return HttpUtil.isKeepAlive(this._request);
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

    @Override
    public Map<String, String> headers() {
        return this._headers;
    }

    @Override
    public ContentType contentType() {
        return this._conentType;
    }

    @Override
    public Charset charset() {
        return this._charset;
    }

    @Override
    public Map<String, List<String>> params() {
        decodeFormBody();
        return this._params;
    }

    @Override
    public <T> T objectParam(Class<T> objectType) {
        decodeObjectBody(objectType);
        return (T) this._objParam;
    }

    @Override
    public String textParam() {
        decodeTextBody();
        return this._textParam;
    }

    @Override
    public void saveBody(OutputStream outputStream) {
        //TODO: save body to a file
        this._logger.error("The method saveBody is not implemented yet!");
    }

    private void decodeTextBody() {
        if (this._bodyParts.size() == 0) {
            return;
        }
        if (this._conentType != ContentType.TEXT) {
            return;
        }

        this._textParam = getBodyString();
    }

    private void decodeFormBody() {
        if (this._bodyParts.size() == 0) {
            return;
        }
        if (this._conentType != ContentType.FORM_URLENCODED) {
            return;
        }

        String formString = getBodyString();
        if (formString.length() == 0) {
            return;
        }
        String[] items = formString.split("&");
        Looper.from(items)
                .map(item -> item.split("="))
                .foreach(kv -> putParam(kv[0], kv[1]));
    }

    private void decodeObjectBody(Class<?> objectType) {
        if (this._bodyParts.size() == 0) {
            return;
        }

        if (this._conentType != ContentType.JSON) {
            return;
        }

        try {
            this._objParam = JSON.std.beanFrom(objectType, getBodyString());
        } catch (IOException ex) {
            throw new KernelException(ex);
        }
    }

    private void putParam(String key, String value) {
        List<String> values = this._params.get(key);
        if (values == null) {
            values = new ArrayList<>();
            this._params.put(key, values);
        }
        values.add(value);
    }

    private String getBodyString() {
        if (!this._lastBodyPart) {
            throw new KernelException("Can't decode http body before last body part is not reached");
        }

        StringBuilder buffer = new StringBuilder();
        Looper.from(this._bodyParts)
                .foreach(part -> {
                    buffer.append(part.toString(this._charset));
                    part.release();
                });
        this._bodyParts.clear();
        return buffer.toString();
    }

    void addTrailer(LastHttpContent httpContent) {
        if (!httpContent.trailingHeaders().isEmpty()) {
            Looper.from(httpContent.trailingHeaders().names())
                    .foreach(name -> {
                        List<String> header = new ArrayList<>();
                        Looper.from(httpContent.trailingHeaders().getAll(name))
                                .foreach(header::add);
                        List<String> existing = this._trailers.get(name);
                        if (existing == null) {
                            this._trailers.put(name, header);
                        } else {
                            existing.addAll(header);
                        }
                    });
        }
        this._lastBodyPart = true;
    }

    void appendBodyPart(HttpContent httpContent) {
        ByteBuf buffer = httpContent.content();
        if (buffer.isReadable()) {
            this._bodyParts.add(buffer);
        }
    }

    int getBodySize() {
        if (this._bodyParts.size() == 0) {
            return 0;
        }
        return Looper.from(this._bodyParts)
                .map(ByteBuf::readableBytes)
                .sum();
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
        buffer.append("TEXT PARAM: ").append(this._textParam).append("\n");
        buffer.append("OBJECT PARAM: ").append(this._objParam).append("\n");
        return buffer.toString();
    }
}
