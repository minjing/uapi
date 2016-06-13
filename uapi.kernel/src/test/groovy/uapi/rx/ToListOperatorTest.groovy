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
 * Test case for ToListOperator
 */
class ToListOperatorTest extends Specification {

    def 'Test get item'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, true, true, true, false]
            getItem() >>> ["1", null, "2", null]
        }

        given:
        ToListOperator opt = new ToListOperator(preOpt)

        expect:
        opt.getItem() == ["1", null, "2", null]
    }
}
