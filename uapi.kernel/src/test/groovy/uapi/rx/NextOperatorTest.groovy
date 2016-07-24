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
 * Test for NextOperator
 */
class NextOperatorTest extends Specification {

    def 'Test getItem'() {
        setup:
        Operator<String> mockOp = Mock(Operator) {
            hasItem() >>> hasItem
            getItem() >>> items
        }

        when:
        def list = new ArrayList()
        NextOperator nextOp = new NextOperator(mockOp, {item -> list.add(item)})
        nextOp.getItem()
        nextOp.getItem()

        then:
        list == items

        where:
        items       | hasItem
        ['1', '2']  | [true, true, false]
    }
}
