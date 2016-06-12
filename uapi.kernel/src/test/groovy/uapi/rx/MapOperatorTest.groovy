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
 * Test for MapOperator
 */
class MapOperatorTest extends Specification {

    def 'Test get item'() {
        when:
        Operator<String> preOpt = Mock(Operator) {
            hasItem() >> next
            getItem() >> data
        }
        MapOperator mapOpt = new MapOperator(preOpt, {item -> Integer.parseInt(item)});

        then:
        mapOpt.hasItem() == next
        mapOpt.getItem() == rtn

        where:
        next    | data  | rtn
        true    | "1"   | 1
        true    | "2"   | 2
        false   | null  | null
    }
}
