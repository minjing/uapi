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
 * Test case for FlatMapOperator
 */
class FlatMapOperatorTest extends Specification {

    def 'Test get item'() {
        def preOpt = Mock(Operator) {
            hasItem() >>> [true, true, true, true, false]
            getItem() >>> ["1,2,3", "4,5,6"]
        }

        given:
        FlatMapOperator opt = new FlatMapOperator(preOpt, {item -> Looper.from(item.split(","))})

        expect:
        opt.getItem() == "1"
        opt.getItem() == "2"
        opt.getItem() == "3"
        opt.getItem() == "4"
        opt.getItem() == "5"
        opt.getItem() == "6"
        opt.getItem() == null
    }
}
