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
 * Test for SingleOperator
 */
class SingleOperatorTest extends Specification {

    def 'Test Get Item'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, false]
            getItem() >>> ["1"]
        }

        given:
        SingleOperator opt = new SingleOperator(preOpt)

        expect:
        opt.getItem() == "1"
        ! opt.hasItem()
    }

    def 'Test Get Item with default'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >> false
            getItem() >> {throw new NoItemException()}
        }

        given:
        SingleOperator opt = new SingleOperator(preOpt, 0)

        expect:
        opt.getItem() == 0
    }

    def 'Test Get Item no item'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >> false
            getItem() >> {throw new NoItemException()}
        }

        given:
        SingleOperator opt = new SingleOperator(preOpt)

        when:
        opt.getItem()

        then:
        thrown(NoItemException)
    }

    def 'Test Get Item no item2'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >> true
            getItem() >> {throw new NoItemException()}
        }

        given:
        SingleOperator opt = new SingleOperator(preOpt)

        when:
        opt.getItem()

        then:
        thrown(NoItemException)
    }

    def 'Test Get Item no item3'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >> true
            getItem() >> {throw new NoItemException()}
        }

        given:
        SingleOperator opt = new SingleOperator(preOpt, 0)

        expect:
        opt.getItem() == 0
    }

    def 'Test Get Item more item'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, true]
            getItem() >>> ["1", "2"]
        }

        given:
        SingleOperator opt = new SingleOperator(preOpt)

        when:
        opt.getItem()

        then:
        thrown(MoreItemException)
    }
}
