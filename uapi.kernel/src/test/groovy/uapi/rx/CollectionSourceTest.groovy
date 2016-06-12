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
 * Test for CollectionSource
 */
class CollectionSourceTest extends Specification {

    def 'Test has item'() {
        when:
        CollectionSource arrSource = new CollectionSource(input)

        then:
        arrSource.hasItem() == checkItem

        where:
        input                       | checkItem
        ["1", "2"] as Collection    | true
    }

    def 'Test null item'() {
        when:
        CollectionSource arrSource = new CollectionSource(input)

        then:
        arrSource.hasItem() == checkItem1
        arrSource.getItem() == first
        arrSource.hasItem() == checkItem2
        arrSource.getItem() == second
        arrSource.hasItem() == checkItem3
        arrSource.getItem() == third

        where:
        input                       | checkItem1    | checkItem2    | checkItem3    | first | second    | third
        ["1", null] as Collection   | true          | true          | false         | "1"   | null      | null
    }
}
