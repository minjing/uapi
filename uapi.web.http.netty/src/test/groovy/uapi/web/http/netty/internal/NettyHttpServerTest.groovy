/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http.netty.internal

import spock.lang.Specification
import uapi.log.ILogger

/**
 * Test for NettyHttpServer
 */
class NettyHttpServerTest extends Specification {

    def NettyHttpServer server

    def setup() {
        server = new NettyHttpServer();
        server._host = 'localhost'
        server._port = 8080
        server._logger = Mock(ILogger)
    }

    def cleanup() {
        server.stop()
    }

    def 'Test start'() {
        when:
        server.start()

        then:
        server.isStarted()
    }

    def 'Test stop'() {
        when:
        server.start()
        server.stop()

        then:
        ! server.isStarted()
    }
}
