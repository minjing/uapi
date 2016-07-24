package uapi.rx

import spock.lang.Specification

/**
 * Test for NextOperator
 */
class NextOperatorTest extends Specification {

    def 'Test getItem'() {
        setup:
        Operator<String> mockOp = Mock(Operator) {
            hasItem() >>> hasItem
            getItem() >>> items
        }

        when:
        def list = new ArrayList()
        NextOperator nextOp = new NextOperator(mockOp, {item -> list.add(item)})
        nextOp.getItem()
        nextOp.getItem()

        then:
        list == items

        where:
        items       | hasItem
        ['1', '2']  | [true, true, false]
    }
}
