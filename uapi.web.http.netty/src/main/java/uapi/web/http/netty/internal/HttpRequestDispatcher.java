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
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import uapi.helper.StringHelper;
import uapi.rx.Looper;

import java.util.List;
import java.util.Map;

/**
 * Created by xquan on 8/5/2016.
 */
class HttpRequestDispatcher extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        StringBuilder buffer = new StringBuilder();
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            HttpHeaders headers = request.headers();
            Looper.from(headers.iteratorAsString())
                    .map(entry -> StringHelper.makeString("HEADER: {}={}\r\n", entry.getKey(), entry.getValue()))
                    .next(buffer::append)
                    .foreach(entry -> buffer.append("\r\n"));

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            Looper.from(params.entrySet())
                    .next(entry -> buffer.append("QUERY: ").append(entry.getKey()))
                    .next(entry -> Looper.from(entry.getValue()).foreach(buffer::append))
                    .foreach(entry -> buffer.append("\r\n"));

            DecoderResult result = request.decoderResult();
            if (result.isSuccess()) {
                return;
            }
        }
    }
}
