/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal

import io.netty.channel.ChannelHandlerContext
import spock.lang.Specification
import uapi.web.http.HttpVersion

/**
 * Test for NettyResponse
 */
class NettyHttpResponseTest extends Specification {

    def 'Test flush'() {
        def request = Mock(NettyHttpRequest) {
            version() >> httpVer
        }
        def ctx = Mock(ChannelHandlerContext)

        when:
        NettyHttpResponse response = new NettyHttpResponse(ctx, request)
        response.write(msg)
        response.flush()

        then:
        1 * ctx.writeAndFlush(_)

        where:
        msg     | httpVer
        'abc'   | HttpVersion.V_1_1
    }
}
