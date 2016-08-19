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
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import uapi.KernelException;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.web.http.IHttpHandler;

import java.util.Map;

/**
 * Created by xquan on 8/5/2016.
 */
class HttpRequestDispatcher extends ChannelInboundHandlerAdapter {

    private final ILogger _logger;

    private final Map<String, IHttpHandler> _handlers;

//    private HttpRequest _request;
//    private final StringBuilder _buffer = new StringBuilder();

//    HttpRequestDispatcher(ILogger logger) {
//        this._logger = logger;
//    }

    HttpRequestDispatcher(ILogger logger, Map<String, IHttpHandler> handlers) {
        this._logger = logger;
        this._handlers = handlers;
    }

    @Override
    public boolean isSharable() {
        return true;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpMessage) {
                NettyHttpRequest request = new NettyHttpRequest((HttpMessage) msg);
                NettyHttpResponse response = new NettyHttpResponse(ctx, request);

                Looper.from(this._handlers.entrySet())
                        .map(Map.Entry::getValue)
                        .foreach(handler -> {
                            switch (request.method()) {
                                case GET:
                                    handler.get(request, response);
                                    break;
                                case PUT:
                                    handler.put(request, response);
                                    break;
                                case POST:
                                    handler.post(request, response);
                                    break;
                                case DELETE:
                                    handler.delete(request, response);
                                    break;
                                default:
                                    throw new KernelException("Unsupported http method {}", request.method());
                            }
                        });
//                response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=UTF-8");
//                response.write(request.toString());
                response.flush();
            } else {
                super.channelRead(ctx, msg);
            }
//            if (msg instanceof HttpContent) {
//                this._logger.error("Unsupported HttpContent branch");
//            }
        } catch (Exception ex) {
            this._logger.error(ex);
            FullHttpMessage response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.copiedBuffer(ex.toString(), CharsetUtil.UTF_8));
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
