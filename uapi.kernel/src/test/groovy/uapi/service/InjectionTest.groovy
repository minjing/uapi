/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service

import spock.lang.Specification
import uapi.KernelException

/**
 * Test case for Injection
 */
class InjectionTest extends Specification {

    def 'Test checkType'() {
        setup:
        Injection injection = new Injection(id, obj)

        expect:
        injection.checkType(type)

        where:
        id      | obj       | type
        'id'    | 'String'  | String.class
    }

    def 'Test checkType with Exception'() {
        setup:
        Injection injection = new Injection(id, obj)

        when:
        injection.checkType(type)

        then:
        thrown(KernelException)

        where:
        id      | obj       | type
        'id'    | 'String'  | Integer.class
    }
}
