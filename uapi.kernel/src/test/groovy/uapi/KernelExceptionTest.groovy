/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi

import spock.lang.Specification

/**
 * Test case for KernelException
 */
class KernelExceptionTest extends Specification {

    def 'Test message construction'() {
        given:
        KernelException ex = new KernelException(template, arg1, arg2)

        expect:
        ex.getMessage() == result

        where:
        template        | arg1  | arg2  | result
        "tet {} tt {}"  | "aa"  | "bb"  | "tet aa tt bb"
    }
}
