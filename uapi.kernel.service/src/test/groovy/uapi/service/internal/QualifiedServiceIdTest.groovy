/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
