package uapi.rx

import spock.lang.Specification
import uapi.helper.Pair

/**
 * Test case for ToMapOperator
 */
class ToMapOperatorTest extends Specification {

    def 'Test getItem method'() {
        def Operator<String> preOpt = Mock(Operator) {
            hasItem() >>> [true, true, true, true, false]
            getItem() >>> [new Pair("1", "2"), new Pair("2", "3"), new Pair("3", "4"), null]
        }

        given:
        ToMapOperator opt = new ToMapOperator(preOpt)

        expect:
        opt.getItem() == ["1":"2", "2":"3", "3":"4"]
    }
}
