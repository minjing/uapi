package uapi.service.internal

import spock.lang.Specification

/**
 * Test case for QualifiedServiceId
 */
class QualifiedServiceIdTest extends Specification {

    def 'Test isAssignTo method'() {
        expect:
        def qsId1 = QualifiedServiceId.splitTo(id1)
        def qsId2 = QualifiedServiceId.splitTo(id2)
        qsId1.isAssignTo(qsId2) == result

        where:
        id1         | id2           | result
        'a@Local'   | 'b@Local'     | false
    }
}
