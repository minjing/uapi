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
 * Created by min on 16/6/12.
 */
class IteratorTest extends Specification {

    def 'test iterator from array'() {
        when:
        List<Integer> list = new ArrayList<>();
        Iterator.from("1", "2", "3").map({item -> Integer.parseInt(item)}).foreach({item -> list.add(item)})

        then:
        list.size() == size
        list.get(0) == first
        list.get(1) == second
        list.get(2) == third

        where:
        size    | first     | second    | third
        3       | 1         | 2         | 3
    }
}
