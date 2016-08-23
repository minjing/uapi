/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.LastHttpContent
import spock.lang.Specification
import uapi.log.ILogger
import uapi.web.http.IHttpHandler
import uapi.web.http.IHttpRequest
import uapi.web.http.IHttpResponse

/**
 * Test for HttpRequestDispatcher
 */
class HttpRequestDispatcherTest extends Specification {

    def 'Test channelRead for get'() {
        IHttpHandler handler = Mock(IHttpHandler) {
            getUriMapping() >> mappedUri
        }

        when:
        HttpRequestDispatcher dispatcher = new HttpRequestDispatcher(Mock(ILogger), [ handler ] as List)
        def ChannelHandlerContext handlerCtx = Mock(ChannelHandlerContext)
        def HttpRequest httpReq = Mock(HttpRequest) {
            headers() >> Mock(HttpHeaders) {
                iterator() >> Mock(Iterator) {
                    hasNext() >>> [true, true, false]
                    next() >> Mock(Map.Entry) {
                        getKey() >> headerName
                        getValue() >> headerValue
                    }
                }
            }
            protocolVersion() >> HttpVersion.HTTP_1_1
            method() >> httpMethod
            uri() >> mappedUri
        }
        def LastHttpContent httpContent = Mock(LastHttpContent) {
            content() >> Mock(ByteBuf) {
                isReadable() >> true
                readableBytes() >> 0
            }
            trailingHeaders() >> Mock(HttpHeaders) {
                isEmpty() >> true
            }
        }
        dispatcher.channelRead(handlerCtx, httpReq)
        dispatcher.channelRead(handlerCtx, httpContent)

        then:
        getCall * handler.get(_ as IHttpRequest, _ as IHttpResponse)
        postCall * handler.post(_ as IHttpRequest, _ as IHttpResponse)
        putCall * handler.put(_ as IHttpRequest, _ as IHttpResponse)
        deleteCall * handler.delete(_ as IHttpRequest, _ as IHttpResponse)

        where:
        headerName      | headerValue           | tailingHeaderNamer    | trailingHeaderValue   | mappedUri | httpMethod        | getCall   | postCall  | putCall   | deleteCall
        'content-type'  | 'application/json'    | 'aaa'                 | 'bbb'                 | '/b'      | HttpMethod.GET    | 1         | 0         | 0         | 0
        'content-type'  | 'application/json'    | 'aaa'                 | 'bbb'                 | '/b'      | HttpMethod.POST   | 0         | 1         | 0         | 0
        'content-type'  | 'application/json'    | 'aaa'                 | 'bbb'                 | '/b'      | HttpMethod.PUT    | 0         | 0         | 1         | 0
        'content-type'  | 'application/json'    | 'aaa'                 | 'bbb'                 | '/b'      | HttpMethod.DELETE | 0         | 0         | 0         | 1
    }
}
