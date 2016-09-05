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
import uapi.helper.Pair

/**
 * Test for Operator
 */
class OperatorTest extends Specification {

    def 'Test map'() {
        given:
        TestOp op = new TestOp()

        when:
        def newOp = op.map({ -> return null})

        then:
        newOp != null
        newOp instanceof MapOperator
    }

    def 'Test flatmap'() {
        given:
        TestOp op = new TestOp()

        when:
        def newOp = op.flatmap({ -> return null})

        then:
        newOp != null
        newOp instanceof FlatMapOperator
    }

    def 'Test filter'() {
        given:
        TestOp op = new TestOp()

        when:
        def newOp = op.filter({ -> return null})

        then:
        newOp != null
        newOp instanceof FilterOperator
    }

    def 'Test limit'() {
        given:
        TestOp op = new TestOp()

        when:
        def newOp = op.limit(1)

        then:
        newOp != null
        newOp instanceof LimitOperator
    }

    def 'Test next'() {
        given:
        TestOp op = new TestOp()

        when:
        def newOp = op.next({ -> return null})

        then:
        newOp != null
        newOp instanceof NextOperator
    }

    def 'Test foreach'() {
        given:
        TestOp op = new TestOp()

        expect:
        op.foreach({ item -> })
    }

    def 'Test foreach with index'() {
        given:
        TestOp op = new TestOp()

        expect:
        op.foreachWithIndex({ idx, item -> })
    }

    def 'Test first'() {
        given:
        TestOp op = new TestOp()
        op.item = "1"

        when:
        def res = op.first()

        then:
        res == "1"
    }

    def 'Test first with default'() {
        given:
        TestOp op = new TestOp()
        op.first = false
        op.item = "1"

        when:
        def res = op.first("0")

        then:
        res == "0"
    }

    def 'Test single'() {
        given:
        TestOp op = new TestOp()
        op.item = "1"

        when:
        def res = op.single()

        then:
        res == "1"
    }

    def 'Test single with default'() {
        given:
        TestOp op = new TestOp()
        op.first = false
        op.item = "1"

        when:
        def res = op.single("0")

        then:
        res == "0"
    }

    def 'Test sum'() {
        given:
        TestOp op = new TestOp()
        op.item = 1

        when:
        def res = op.sum()

        then:
        res == 1
    }

    def 'Test toList'() {
        given:
        TestOp op = new TestOp()
        op.item = 1

        when:
        def res = op.toList()

        then:
        res == [1]
    }

    def 'Test toMap'() {
        given:
        TestOp op = new TestOp()
        op.item = new Pair('1', '2')

        when:
        def res = op.toMap()

        then:
        res == ['1': '2']
    }

    class TestOp extends Operator {

        Object item
        boolean first=true

        @Override
        boolean hasItem() {
            if (first) {
                first = false
                return true
            } else {
                return false
            }
        }

        @Override
        def Object getItem() throws NoItemException {
            return item
        }

        @Override
        void done() { }
    }

    class TestOp2 extends Operator<Pair<String, String>> {

        Pair<String, String> item
        boolean first=true

        @Override
        boolean hasItem() {
            if (first) {
                first = false
                return true
            } else {
                return false
            }
        }

        @Override
        def Pair<String, String> getItem() throws NoItemException {
            return item
        }

        @Override
        void done() { }
    }
}
