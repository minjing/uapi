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
 * Test case for FirstOperator
 */
class FirstOperatorTest extends Specification {

    def 'Test get item'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, true, true, true, false]
            getItem() >>> ["1", null, "2", null]
        }

        given:
        FirstOperator opt = new FirstOperator(preOpt)

        expect:
        opt.getItem() == "1"
//        opt.getItem() == null
//        opt.getItem() == null
        ! opt.hasItem()
    }

    def 'Test get item with default'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [false]
        }

        given:
        FirstOperator opt = new FirstOperator(preOpt, "1")

        expect:
        opt.getItem() == "1"
        ! opt.hasItem()
    }
}
