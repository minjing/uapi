/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service

import spock.lang.Specification
import uapi.service.internal.QualifiedServiceId

/**
 * Test for DependencyInfo
 */
class DependencyInfoTest extends Specification {

    def 'Test create instance'() {
        setup:
        DependencyInfo info = new DependencyInfo(name, type)

        expect:
        info.fieldName == name
        info.fieldType == type
        info.injectFrom == from
        info.dependencyId == type
        info.generateField == genField

        where:
        name    | type      | from                          | genField
        'name'  | 'type'    | QualifiedServiceId.FROM_ANY   | false
    }

    def 'Test create instance2'() {
        setup:
        DependencyInfo info = new DependencyInfo(name, type, genField)

        expect:
        info.fieldName == name
        info.fieldType == type
        info.injectFrom == from
        info.dependencyId == type
        info.generateField == genField

        where:
        name    | type      | from                          | genField
        'name'  | 'type'    | QualifiedServiceId.FROM_ANY   | true
    }
}
