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

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.type.TypeMirror

/**
 * Test for ParameterMeta
 */
class ParameterMetaTest extends Specification {

    def 'Test build'() {
        when:
        ParameterMeta paramMet = ParameterMeta.builder()
            .setName(name)
            .setType(type)
            .addModifier(Modifier.PUBLIC)
            .addModifier(Modifier.FINAL)
            .build()

        then:
        paramMet.getName() == name
        paramMet.getType() == type
        paramMet.getModifiers() == modifies

        where:
        name    | type      | modify1           | modify2           | modifies
        'Test'  | 'String'  | Modifier.PUBLIC   | Modifier.FINAL    | "public final"
    }

    def 'Test build from Element'() {
        def mockName = Mock(Name) {
            toString() >> name
        }
        def mockType = Mock(TypeMirror) {
            toString() >> type
        }
        def mockElement = Mock(Element) {
            getKind() >> ElementKind.PARAMETER
            getSimpleName() >> mockName
            asType() >> mockType
        }
        def mockBuilderCtx = Mock(IBuilderContext)

        when:
        ParameterMeta paramMeta = ParameterMeta.builder(mockElement, mockBuilderCtx).build()

        then:
        paramMeta.getName() == name
        paramMeta.getType() == type

        where:
        name    | type
        'Test'  | 'String'
    }

    def 'Test equals'() {
        when:
        ParameterMeta.Builder paramBuilder1 = ParameterMeta.builder()
                .setName(name)
                .setType(type)
                .addModifier(Modifier.PUBLIC)
                .addModifier(Modifier.FINAL)
        ParameterMeta.Builder paramBuilder2 = ParameterMeta.builder()
                .setName(name)
                .setType(type)
                .addModifier(Modifier.PUBLIC)
                .addModifier(Modifier.FINAL)
        ParameterMeta.Builder paramBuilder3 = ParameterMeta.builder()
                .setName(name)
                .setType(type)
                .addModifier(Modifier.PUBLIC)

        then:
        paramBuilder1 == paramBuilder2
        paramBuilder1 != paramBuilder3
        paramBuilder2 != paramBuilder3
        paramBuilder1.hashCode() == paramBuilder2.hashCode()
        paramBuilder1.hashCode() != paramBuilder3.hashCode()
        paramBuilder2.hashCode() != paramBuilder3.hashCode()

        where:
        name    | type      | modify1           | modify2           | modifies
        'Test'  | 'String'  | Modifier.PUBLIC   | Modifier.FINAL    | "public final"
    }
}
