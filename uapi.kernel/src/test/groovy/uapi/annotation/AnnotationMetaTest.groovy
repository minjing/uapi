/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation

import spock.lang.Specification

/**
 * Test case for AnnotationMeta
 */
class AnnotationMetaTest extends Specification {

    def 'Test build'() {
        MockArgumentMetaBuilder mockParamBuild = new MockArgumentMetaBuilder()

        when:
        AnnotationMeta annoMeta = AnnotationMeta.builder()
            .setName(annotationName)
            .addArgument(mockParamBuild)
            .build()

        then:
        annoMeta.name == annotationName
        annoMeta.arguments.size() == 1
        mockParamBuild._initInvokedCount == 1
        mockParamBuild._initCreateInstCount == 1

        where:
        annotationName  | argSize
        'Test'          | 1
    }

    def mockArgMeta = Mock(ArgumentMeta)

    private class MockArgumentMetaBuilder extends ArgumentMeta.Builder {

        private int _initInvokedCount = 0
        private int _initCreateInstCount = 0

        public void initProperties() {
            this._initInvokedCount++
        }

        public ArgumentMeta createInstance() {
            this._initCreateInstCount++
            return mockArgMeta
        }
    }
}
