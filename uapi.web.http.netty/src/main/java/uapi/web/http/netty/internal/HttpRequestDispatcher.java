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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import uapi.helper.StringHelper;
import uapi.log.ILogger;
import uapi.rx.Looper;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xquan on 8/5/2016.
 */
class HttpRequestDispatcher extends SimpleChannelInboundHandler {

    ILogger _logger;

    private HttpRequest _request;
    private final StringBuilder _buffer = new StringBuilder();

    HttpRequestDispatcher(ILogger logger) {
        this._logger = logger;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this._request = (HttpRequest) msg;

            HttpHeaders headers = request.headers();
            Looper.from(headers.iteratorAsString())
                    .map(entry -> StringHelper.makeString("HEADER: {}={}\r\n", entry.getKey(), entry.getValue()))
                    .next(this._buffer::append)
                    .foreach(entry -> this._buffer.append("\r\n"));

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            Looper.from(params.entrySet())
                    .next(entry -> this._buffer.append("QUERY: ").append(entry.getKey()))
                    .next(entry -> Looper.from(entry.getValue()).foreach(this._buffer::append))
                    .foreach(entry -> this._buffer.append("\r\n"));

            DecoderResult result = request.decoderResult();
            if (result.isSuccess()) {
                return;
            }
        }
        if (msg instanceof HttpContent) {
            this._logger.error("Unsupported HttpContent branch");
        }
    }

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        boolean keepAlive = HttpUtil.isKeepAlive(this._request);
        FullHttpMessage response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                currentObj.decoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer(this._buffer.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        String cookieString = this._request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
        }

        ctx.write(response);

        return keepAlive;
    }
}
