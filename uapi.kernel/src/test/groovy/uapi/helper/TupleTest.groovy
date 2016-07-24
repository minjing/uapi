/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper

import spock.lang.Specification
import uapi.InvalidArgumentException
import uapi.KernelException

/**
 * Test for Tuple
 */
class TupleTest extends Specification {

    def 'Test splitTo'() {
        when:
        Tuple tuple = Tuple.splitTo(string, sep)

        then:
        tuple.leftValue == left
        tuple.rightValue == right

        where:
        string  | sep   | left  | right
        'a,b'   | ','   | 'a'   | 'b'
        'a'     | ','   | 'a'   | StringHelper.EMPTY
    }

    def 'Test splitTo exception'() {
        when:
        Tuple.splitTo(string, sep)

        then:
        thrown(ex)

        where:
        string  | sep   | ex
        'a,b,c' | ','   | InvalidArgumentException
    }
}
