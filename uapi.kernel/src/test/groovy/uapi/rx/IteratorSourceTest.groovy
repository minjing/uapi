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
 * Test for IteratorSource
 */
class IteratorSourceTest extends Specification {

    def 'Test getItem'() {
        setup:
        Iterator<String> mockIt = (items as Collection).iterator()

        when:
        IteratorSource itSrc = new IteratorSource(mockIt)

        then:
        itSrc.getItem() == item1
        itSrc.getItem() == item2
        itSrc.getItem() == item3

        where:
        item1   | item2 | item3 | items
        '1'     | '2'   | null  | ['1', '2']
    }
}
