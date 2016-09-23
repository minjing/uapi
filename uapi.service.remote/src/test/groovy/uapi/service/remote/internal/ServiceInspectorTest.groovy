/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal

import spock.lang.Specification

/**
 * Unit test for ServiceInspector
 */
class ServiceInspectorTest extends Specification {

    def 'Test inspect'() {
        given:
        ServiceInspector svcInspector = new ServiceInspector()

        when:
        def svcMeta = svcInspector.inspect('sid', Test.class)

        then:
        svcMeta != null
    }

    public interface Test {

        String getName(String test)
    }
}
