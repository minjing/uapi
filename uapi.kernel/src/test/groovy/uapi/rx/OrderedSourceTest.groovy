/**
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
 * Test for OrderedSourceTest
 */
class OrderedSourceTest extends Specification {

    def 'Test has item'() {
        when:
        OrderedSource listSource = new OrderedSource(input)

        then:
        listSource.hasItem() == checkItem

        where:
        input       | checkItem
        ["1", "2"]  | true
    }

    def 'Test null item'() {
        when:
        OrderedSource listSource = new OrderedSource(input)

        then:
        listSource.hasItem() == checkItem1
        listSource.getItem() == first
        listSource.hasItem() == checkItem2
        listSource.getItem() == second
        listSource.hasItem() == checkItem3
        listSource.getItem() == third

        where:
        input           | checkItem1    | checkItem2    | checkItem3    | first | second    | third
        ["1", null]     | true          | true          | false         | "1"   | null      | null
    }
}
