/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal

import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpRequest
import io.netty.util.CharsetUtil
import spock.lang.Specification
import uapi.log.ILogger
import uapi.web.http.ContentType
import uapi.web.http.HttpMethod
import uapi.web.http.HttpVersion

/**
 * Test for NettyHttpRequest
 */
class NettyHttpRequestTest extends Specification {

    def 'Test creation'() {
        def req = Mock(HttpRequest) {
            uri() >> reqUri
            headers() >> Mock(HttpHeaders) {
                iterator() >> Mock(Iterator) {
                    hasNext() >>> [true, true, false]
                    next() >> Mock(Map.Entry) {
                        getKey() >> headerName
                        getValue() >> headerValue
                    }
                }
            }
            method() >> nettyReqMethod
            protocolVersion() >> nettyHttpVer
        }

        when:
        NettyHttpRequest nettyReq = new NettyHttpRequest(Mock(ILogger), req)

        then:
        nettyReq.uri() == reqUri
        nettyReq.headers().size() == 1
        nettyReq.headers().get(headerName) == headerValue
        nettyReq.method() == reqMethod
        nettyReq.contentType() == ContentType.XML
        nettyReq.charset() == CharsetUtil.UTF_8
        nettyReq.version() == httpVer
        nettyReq.params().size() == paramSize
        nettyReq.params().get(paramName).get(0) == paramValue

        where:
        reqUri      | paramSize | paramName | paramValue    | headerSize    | headerName        | headerValue                   | httpVer           | nettyHttpVer                                      | reqMethod      | nettyReqMethod
        '/b?a=c'    | 1         | 'a'       | 'c'           | 1             | 'content-type'    | 'text/xml ; charset=UTF-8'    | HttpVersion.V_1_1 | io.netty.handler.codec.http.HttpVersion.HTTP_1_1  | HttpMethod.GET | io.netty.handler.codec.http.HttpMethod.GET
    }
}
