/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx

import rx.Observable
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test case for Iterator
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

    @Ignore
    def 'test rv vx. rxJava'() {
        List<Integer> list = new ArrayList<>();
        for (id in 1..1000000) {
            list.add(id)
        }

        given:
        List<String> strList2 = new ArrayList<>()
        long start2 = System.currentTimeMillis()
        Observable.from(list).map({ item -> String.valueOf(item)}).forEach({ item -> strList2.add(item)})
        long end2 = System.currentTimeMillis()
        System.out.println(end2 - start2)

        List<String> strList1 = new ArrayList<>()
        long start1 = System.currentTimeMillis()
        Iterator.from(list).map({item -> String.valueOf(item)}).foreach({item -> strList1.add(item)})
        long end1 = System.currentTimeMillis()
        System.out.println(end1 - start1)

        expect:
        strList1.size() == 1000000
        strList1.get(0) == "1"
        strList1.get(99) == "100"

        strList2.size() == 1000000
        strList2.get(0) == "1"
        strList2.get(99) == "100"
    }
}
