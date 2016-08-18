/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import uapi.log.ILogger;

/**
 * Created by xquan on 8/5/2016.
 */
class HttpRequestDispatcher extends ChannelInboundHandlerAdapter {

    ILogger _logger;

    private HttpRequest _request;
    private final StringBuilder _buffer = new StringBuilder();

    HttpRequestDispatcher(ILogger logger) {
        this._logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpMessage) {
            NettyHttpRequest request = new NettyHttpRequest((HttpMessage) msg);
            NettyHttpResponse response = new NettyHttpResponse(ctx, request);
            response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=UTF-8");
            response.write(request.toString());
            response.flush();
        } else {
            super.channelRead(ctx, msg);
        }
        if (msg instanceof HttpContent) {
            this._logger.error("Unsupported HttpContent branch");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
