/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import uapi.KernelException;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.web.http.IHttpHandler;
import uapi.web.http.ILargeHttpHandler;

import java.util.List;
import java.util.Map;

/**
 * Created by xquan on 8/5/2016.
 */
class HttpRequestDispatcher extends ChannelInboundHandlerAdapter {

    private static final int DEFAULT_BUFFER_SIZE    = 1024 * 1024;  // 1M

    private final ILogger _logger;

    private final Map<String, IHttpHandler> _handlers;

    private final int _maxBufferSize = DEFAULT_BUFFER_SIZE;

    private NettyHttpRequest _request;
    private NettyHttpResponse _response;

    private IHttpHandler _handler;

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
            if (msg instanceof HttpRequest) {
                if (this._request == null) {
                    this._request = new NettyHttpRequest(this._logger, (HttpRequest) msg);
                }
                if (this._response == null) {
                    this._response = new NettyHttpResponse(ctx, this._request);
                }
            }

            // Find out mapped handler
            if (this._handler == null) {
                List<IHttpHandler> handlers = Looper.from(this._handlers.entrySet())
                        .filter(entry -> this._request.uri().startsWith(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .toList();
                if (handlers.size() == 0) {
                    throw new KernelException("No handler is mapped to uri {}", this._request.uri());
                }
                this._handler = handlers.get(0);
                if (handlers.size() > 1) {
                    for (int i = 1; i < handlers.size(); i++) {
                        if (handlers.get(i).getUriMapping().length() > this._handler.getUriMapping().length()) {
                            this._handler = handlers.get(i);
                        }
                    }
                }
            }

            if (this._handler instanceof ILargeHttpHandler) {
                switch (this._request.method()) {
                    case GET:
                        this._handler.get(this._request, this._response);
                        break;
                    case PUT:
                        this._handler.put(this._request, this._response);
                        break;
                    case POST:
                        this._handler.post(this._request, this._response);
                        break;
                    case DELETE:
                        this._handler.delete(this._request, this._response);
                        break;
                    default:
                        throw new KernelException("Unsupported http method {}", this._request.method());
                }
            }

            if (msg instanceof HttpContent) {
                this._request.appendBodyPart((HttpContent) msg);

                // Check body size
                if (this._request.getBodySize() > this._maxBufferSize) {
                    throw new KernelException("The max buffer size has been reached - {}", this._maxBufferSize);
                }

                if (msg instanceof LastHttpContent) {
                    this._request.addTrailer((LastHttpContent) msg);

                    switch (this._request.method()) {
                        case GET:
                            this._handler.get(this._request, this._response);
                            break;
                        case PUT:
                            this._handler.put(this._request, this._response);
                            break;
                        case POST:
                            this._handler.post(this._request, this._response);
                            break;
                        case DELETE:
                            this._handler.delete(this._request, this._response);
                            break;
                        default:
                            throw new KernelException("Unsupported http method {}", this._request.method());
                    }

                    this._response.flush();

                    if (! this._request.isKeepAlive()) {
                        // If keep-alive is off, close the connection once the content is fully written.
                        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                    }
                }
            }
        } catch (Exception ex) {
            this._logger.error(ex);
            FullHttpMessage response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.copiedBuffer(ex.toString(), CharsetUtil.UTF_8));
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response);
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
