/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful.internal

import spock.lang.Specification
import uapi.KernelException
import uapi.annotation.IBuilderContext
import uapi.annotation.LogSupport
import uapi.log.ILogger
import uapi.service.annotation.Service
import uapi.web.restful.annotation.Restful

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

/**
 * Test for RestfulHandler
 */
class RestfulHandlerTest extends Specification {

    def 'Test handled annotations'() {
        given:
        RestfulHandler handler = new RestfulHandler()

        expect:
        handler.getOrderedAnnotations() == [Restful.class]
    }

    def 'Test handle incorrect element'() {
        given:
        def builderCtx = Mock(IBuilderContext) {
            getLogger() >> Mock(LogSupport) {
                1 * error(_ as Exception)
            }
        }
        def element = Mock(Element) {
            getKind() >> ElementKind.ENUM
        }
        def elements = new HashSet()
        elements.add(element)
        RestfulHandler handler = new RestfulHandler()

        expect:
        handler.handleAnnotatedElements(builderCtx, Restful.class, elements)
    }

    def 'Test handle element with incorrect outside element'() {
        given:
        def builderCtx = Mock(IBuilderContext) {
            getLogger() >> Mock(LogSupport) {
                1 * error(_ as Exception)
            }
        }
        def element = Mock(Element) {
            getKind() >> ElementKind.METHOD
            getEnclosingElement() >> Mock(Element) {
                1 * getAnnotation(_)
            }
        }
        def elements = new HashSet()
        elements.add(element)
        RestfulHandler handler = new RestfulHandler()

        expect:
        handler.handleAnnotatedElements(builderCtx, Restful.class, elements)
    }
}
