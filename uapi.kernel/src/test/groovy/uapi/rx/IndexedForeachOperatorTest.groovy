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
 * Test for IndexedForeachOperator
 */
class IndexedForeachOperatorTest extends Specification {

    def 'Test getItem'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, true, false]
            getItem() >>> ["1", "2", null]
        }

        given:
        List<String> list = new ArrayList<>()
        IndexedForeachOperator opt = new IndexedForeachOperator(preOpt, {index, item -> list.add(index, item)});

        expect:
        opt.getItem() == null
        list.size() == 2
        list.get(0) == "1"
        list.get(1) == "2"
    }
}
