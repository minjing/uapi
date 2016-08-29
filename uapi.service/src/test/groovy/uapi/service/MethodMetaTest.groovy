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
import uapi.web.restful.ArgumentMapping
import uapi.web.restful.IndexedArgumentMapping

/**
 * Test case for MethodMeta
 */
class MethodMetaTest extends Specification {

    def 'Test isSame'() {
        when:
        def ArgumentMapping mockArgMapping = Mock(ArgumentMapping)
        mockArgMapping.isSameType(_) >> {
            return true
        }
        def IndexedArgumentMapping mockIdxArg = Mock(IndexedArgumentMapping)
        mockIdxArg.isSameType(_) >> {
            return true
        }
        def MethodMeta methodMeta1 = new MethodMeta(name1, returnType1)
        methodMeta1.addArgumentMeta(mockArgMapping)
        def MethodMeta methodMeta2 = new MethodMeta(name2, returntype2)
        methodMeta2.addArgumentMeta(mockIdxArg)

        then:
        methodMeta1.isSame(methodMeta2)

        where:
        name1       | returnType1           | name2         | returntype2
        "sayHello"  | "java.lang.String"    | "sayHello"    | "java.lang.String"
    }
}
