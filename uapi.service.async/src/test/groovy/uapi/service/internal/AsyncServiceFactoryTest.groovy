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

    def 'Test newBuilder'() {
        given:
        IAsyncServiceBuilder<TestService> builder = new AsyncServiceFactory().newBuilder(TestService.class);
        def realSvc = Mock(TestService)
        def callback = Mock(IAsyncCallback)

        when:
        def proxy = (TestService) builder.on(realSvc).with(callback).build()

        then:
        proxy.getMessage('abc') == null
    }

    public static interface TestService extends IService {

        String getMessage(String msg);
    }

}
