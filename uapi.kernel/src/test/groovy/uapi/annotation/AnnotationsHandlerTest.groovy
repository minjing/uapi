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
import uapi.KernelException

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import java.lang.annotation.Annotation

/**
 * Test for AnnotationsHandler
 */
class AnnotationsHandlerTest extends Specification {

    def handler = new AnnotationsHandler() {

        @Override
        protected Class<? extends Annotation>[] getOrderedAnnotations() {
            return new Class<? extends Annotation>[0]
        }

        @Override
        protected void handleAnnotatedElements(
                IBuilderContext builderContext,
                Class<? extends Annotation> annotationType,
                Set<? extends Element> elements
        ) throws KernelException {

        }
    }

    def 'Test check modifiers'() {
        def mockElemt = Mock(Element) {
            getModifiers() >> supports
            getKind() >> ElementKind.CLASS
            getEnclosingElement() >> Mock(Element) {
                getSimpleName() >> Mock(Name) {
                    toString() >> 'aaa'
                }
            }
            getSimpleName() >> Mock(Name) {
                toString() >> 'bbb'
            }
        }

        when:
        handler.checkModifiers(mockElemt, NotNull, unsupport)

        then:
        thrown(KernelException)

        where:
        supports                                    | unsupport
        [Modifier.PUBLIC, Modifier.FINAL ] as Set   | Modifier.PUBLIC
    }

    def 'Test check annotations'() {
        def mockElemt = Mock(Element) {
            getAnnotation(_) >> null
            getSimpleName() >> Mock(Name) {
                toString() >> 'bbb'
            }
        }

        when:
        handler.checkAnnotations(mockElemt, NotNull)

        then:
        thrown(KernelException)
    }

    def 'Test get type in annotation'() {
        
    }
}
