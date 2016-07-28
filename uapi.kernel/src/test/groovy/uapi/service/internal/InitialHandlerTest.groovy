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
import uapi.annotation.ClassMeta
import uapi.annotation.IBuilderContext
import uapi.annotation.LogSupport
import uapi.service.annotation.Service

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.type.TypeMirror

/**
 * Test for InitialHandler
 */
@Ignore
class InitialHandlerTest extends Specification {

    def 'Test handleAnnotatedElements'() {
        setup:
        IBuilderContext builderCtx = Mock(IBuilderContext) {
            getLogger() >> Mock(LogSupport)
            findClassBuilder(_) >> Mock(ClassMeta.Builder) {
                findMethodBuilders('init') >> [] as List
            }
        }
        Element methodElement = Mock(ExecutableElement) {
            getKind() >> ElementKind.METHOD
            getSimpleName() >> Mock(Name) {
                toString() >> methodName
            }
            getEnclosingElement() >> Mock(Element) {
                getKind() >> ElementKind.CLASS
                getSimpleName() >> Mock(Name) {
                    toString() >> className
                }
            }
            getReturnType() >> Mock(TypeMirror) {
                toString() >> returnType
            }
            getModifiers() >> [Modifier.PUBLIC] as Set
            getThrownTypes() >> [] as List
            getParameters() >> [] as List
        }
        Set elements = [methodElement] as Set
        InitialHandler initHandler = new InitialHandler()

        expect:
        initHandler.handleAnnotatedElements(builderCtx, Service.class, elements)

//        then:

        where:
        methodName      | className     | returnType
        'methodName'    | 'className'   | 'String'
    }
}
