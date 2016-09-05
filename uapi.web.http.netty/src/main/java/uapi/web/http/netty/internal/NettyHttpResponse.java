/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.rx.Looper;
import uapi.web.http.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@Link IHttpResponse} by Netty
 */
class NettyHttpResponse implements IHttpResponse {

    private final ChannelHandlerContext _ctx;
    private final NettyHttpRequest _request;
    private final Map<String, String> _headers = new HashMap<>();
    private final StringBuilder _buffer = new StringBuilder();

    private uapi.web.http.HttpResponseStatus _responseStatus;
    private uapi.web.http.HttpVersion _version;
    private boolean _flashed = false;

    NettyHttpResponse(ChannelHandlerContext ctx, NettyHttpRequest request) {
        if (ctx == null) {
            throw new InvalidArgumentException("ctx", InvalidArgumentException.InvalidArgumentType.EMPTY);
        }
        if (request == null) {
            throw new InvalidArgumentException("request", InvalidArgumentException.InvalidArgumentType.EMPTY);
        }
        this._ctx = ctx;
        this._request = request;
        this._version = request.version();
        this._responseStatus = uapi.web.http.HttpResponseStatus.OK;
    }

    @Override
    public void setVersion(uapi.web.http.HttpVersion version) {
        this._version = version;
    }

    @Override
    public void setStatus(uapi.web.http.HttpResponseStatus status) {
        this._responseStatus = status;
    }

    @Override
    public void setHeader(String key, Object value) {
        if (value == null) {
            throw new InvalidArgumentException("value", InvalidArgumentException.InvalidArgumentType.EMPTY);
        }
        setHeader(key, value.toString());
    }

    @Override
    public void setHeader(String key, int value) {
        setHeader(key, Integer.valueOf(value).toString());
    }

    @Override
    public void setHeader(String key, float value) {
        setHeader(key, Float.valueOf(value).toString());
    }

    @Override
    public void setHeader(String key, String value) {
        this._headers.put(key, value);
    }

    @Override
    public void write(String message) {
        this._buffer.append(message);
    }

    @Override
    public void flush() {
        // TODO: need support flush multiple time to support large data response
        if (this._flashed) {
            throw new KernelException("The flash method must be invoked only once");
        }
        HttpVersion httpVer;
        if (this._version == uapi.web.http.HttpVersion.V_1_0) {
            httpVer = HttpVersion.HTTP_1_0;
        } else if (this._version == uapi.web.http.HttpVersion.V_1_1) {
            httpVer = HttpVersion.HTTP_1_1;
        } else {
            throw new KernelException("Unsupported http version {}", this._version);
        }

        FullHttpMessage response = new DefaultFullHttpResponse(
                httpVer,
                HttpResponseStatus.valueOf(this._responseStatus.getCode()),
                Unpooled.copiedBuffer(this._buffer.toString(), CharsetUtil.UTF_8));

        if (this._request.isKeepAlive()) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        Looper.from(this._headers.entrySet())
                .foreach(entry -> response.headers().set(entry.getKey(), entry.getValue()));
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        this._headers.clear();
        this._buffer.delete(0, this._buffer.length());

        this._ctx.writeAndFlush(response);
        this._flashed = true;
    }
}
