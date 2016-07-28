/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal

import spock.lang.Ignore
import spock.lang.Specification
import uapi.KernelException
import uapi.annotation.ClassMeta
import uapi.annotation.IBuilderContext
import uapi.annotation.LogSupport

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Name

/**
 * Test for OptionalParser
 */
class OptionalParserTest extends Specification {

    def 'Test parse non-field element'() {
        setup:
        def budrCtx = Mock(IBuilderContext) {
            getLogger() >> Mock(LogSupport)
        }
        def fieldElmt = Mock(Element) {
            getKind() >> elementType
            getSimpleName() >> Mock(Name) {
                toString() >> fieldName
            }
        }
        def elmts = [fieldElmt] as Set
        OptionalParser parser = new OptionalParser()

        when:
        parser.parse(budrCtx, elmts)

        then:
        thrown(KernelException)

        where:
        elementType         | fieldName
        ElementKind.METHOD  | 'fieldName'
    }

    @Ignore
    def 'Test parse element'() {
        setup:
        def budrCtx = Mock(IBuilderContext) {
            getLogger() >> Mock(LogSupport)
            findClassBuilder(_) >> Mock(ClassMeta.Builder) {

            }
        }
        def clsElmt = Mock(Element) {

        }
        def fieldElmt = Mock(Element) {
            getKind() >> elementType
            getSimpleName() >> Mock(Name) {
                toString() >> fieldName
            }
            getEnclosingElement() >> clsElmt
        }
        def elmts = [fieldElmt] as Set
        OptionalParser parser = new OptionalParser()

        when:
        parser.parse(budrCtx, elmts)

        then:
        thrown(KernelException)

        where:
        elementType         | fieldName
        ElementKind.FIELD   | 'fieldName'
    }
}
