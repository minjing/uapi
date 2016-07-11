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

import javax.lang.model.element.Modifier

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
}
