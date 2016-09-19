/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal

import spock.lang.Specification
import uapi.service.IAsyncCallback
import uapi.service.IAsyncServiceBuilder
import uapi.service.IService

/**
 * Unit test for AsyncServiceFactory
 */
class AsyncServiceFactoryTest extends Specification {

    def factory
    def builder

    def setup() {
        factory = new AsyncServiceFactory()
        factory._timeOfCheck = 10
        factory.init()
        builder = factory.newBuilder(TestService.class)
    }

    def cleanup() {
        factory.onUnload()
    }

    def 'Test newBuilder'() {
        given:
        def realSvc = Mock(TestService) {
            getMessage(arg) >> resut
        }
        def callback = Mock(IAsyncCallback)

        when:
        def proxy = builder.on(realSvc).with(callback).build()
        proxy.getMessage(arg) == null

        then:
        1 * callback.calling(_ as String, 'getMessage', _ as Object[])
        1 * callback.succeed(_ as String, resut)

        where:
        arg     | resut
        'aa'    | 'bb'
    }

    public static interface TestService extends IService {

        String getMessage(String msg);
    }

}
