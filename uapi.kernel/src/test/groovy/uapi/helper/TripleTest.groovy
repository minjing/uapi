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

/**
 * Test for Triple
 */
class TripleTest extends Specification {

    def 'Test splitTo'() {
        when:
        Triple triple = Triple.splitTo(string, sep)

        then:
        triple.leftValue == left
        triple.centerValue == center
        triple.rightValue == right

        where:
        string  | sep   | left  | center                | right
        'a,b,c' | ','   | 'a'   | 'b'                   | 'c'
        'a'     | ','   | 'a'   | StringHelper.EMPTY    | StringHelper.EMPTY
        'a,b'   | ','   | 'a'   | 'b'                   | StringHelper.EMPTY
    }

    def 'Test splitTo Exception'() {
        when:
        Triple.splitTo(string, sep)

        then:
        thrown(ex)

        where:
        string      | sep   | ex
        'a,b,c,c'   | ','   | InvalidArgumentException
    }
}
