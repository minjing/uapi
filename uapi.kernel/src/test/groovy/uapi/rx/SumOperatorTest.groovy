/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx

import spock.lang.Specification

/**
 * Test for SumOperator
 */
class SumOperatorTest extends Specification {

    def 'Test getItem'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, true, true, false]
            getItem() >>> [1, 2, 3, null]
        }

        when:
        SumOperator opt = new SumOperator(preOpt)

        then:
        opt.getItem() == 6
    }
}
