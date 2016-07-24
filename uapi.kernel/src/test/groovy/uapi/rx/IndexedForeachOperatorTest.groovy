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
