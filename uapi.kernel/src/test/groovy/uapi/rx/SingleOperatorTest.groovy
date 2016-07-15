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
 * Test for SingleOperator
 */
class SingleOperatorTest extends Specification {

    def 'Test Get Item'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, false]
            getItem() >>> ["1"]
        }

        given:
        FirstOperator opt = new FirstOperator(preOpt)

        expect:
        opt.getItem() == "1"
        ! opt.hasItem()
    }
}
