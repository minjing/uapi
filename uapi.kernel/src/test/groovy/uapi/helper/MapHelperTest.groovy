/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper

import spock.lang.Specification

/**
 * Test for MapHelper
 */
class MapHelperTest extends Specification {

    def 'Test findKey'() {
        expect:
        MapHelper.findKey(map, keys) == key

        where:
        map                     | keys                      | key
        ["1": "a", "2": "b"]    | ['1', '2'] as String[]    | '1'
        ["3": "a", "2": "b"]    | ['1', '2'] as String[]    | '2'
    }

    def 'Test asString'() {
        expect:
        MapHelper.asString(map) == mapString

        where:
        map                         | mapString
        ["1": "a", "2": "b"]        | '{1=a,2=b}'
        ["1": "a", "2": ['3':'c']]  | '{1=a,2={3=c}}'
    }
}
