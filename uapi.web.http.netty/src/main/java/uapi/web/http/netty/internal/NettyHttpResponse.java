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
import io.netty.util.CharsetUtil;
import uapi.InvalidArgumentException;
import uapi.rx.Looper;
import uapi.web.http.IHttpResponse;

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

    NettyHttpResponse(ChannelHandlerContext ctx, NettyHttpRequest request) {
        if (ctx == null) {
            throw new InvalidArgumentException("ctx", InvalidArgumentException.InvalidArgumentType.EMPTY);
        }
        if (request == null) {
            throw new InvalidArgumentException("request", InvalidArgumentException.InvalidArgumentType.EMPTY);
        }
        this._ctx = ctx;
        this._request = request;
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
        FullHttpMessage response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(this._buffer.toString(), CharsetUtil.UTF_8));

        if (this._request.isKeepAlive()) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        Looper.from(this._headers.entrySet())
                .foreach(entry -> response.headers().set(entry.getKey(), entry.getValue()));

        this._headers.clear();
        this._buffer.delete(0, this._buffer.length());

        this._ctx.writeAndFlush(response);
    }
}
