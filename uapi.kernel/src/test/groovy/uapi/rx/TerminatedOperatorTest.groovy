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
import uapi.KernelException

/**
 * Tests for TerminatedOperator
 */
class TerminatedOperatorTest extends Specification {

    def 'Test map method'() {
        given:
        def Operator<String> preOpt = Mock(Operator) { }
        def TestOp op = new TestOp(preOpt)

        when:
        op.map(null)

        then:
        thrown(KernelException)
    }

    def 'Test filter method'() {
        given:
        def Operator<String> preOpt = Mock(Operator) { }
        def TestOp op = new TestOp(preOpt)

        when:
        op.filter(null)

        then:
        thrown(KernelException)
    }

    def 'Test limit method'() {
        given:
        def Operator<String> preOpt = Mock(Operator) { }
        def TestOp op = new TestOp(preOpt)

        when:
        op.limit(1)

        then:
        thrown(KernelException)
    }

    def 'Test foreach method'() {
        given:
        def Operator<String> preOpt = Mock(Operator) { }
        def TestOp op = new TestOp(preOpt)

        when:
        op.foreach(null)

        then:
        thrown(KernelException)
    }

    class TestOp extends TerminatedOperator {

        def TestOp(Operator previously) {
            super(previously)
        }

        @Override
        def Object getItem() throws NoItemException {
            return null
        }
    }
}
