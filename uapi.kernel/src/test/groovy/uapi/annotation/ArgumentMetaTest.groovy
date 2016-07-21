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
 * Test for ArgumentMeta
 */
class ArgumentMetaTest extends Specification {

    def 'Test build'() {
        when:
        ArgumentMeta argMeta = ArgumentMeta.builder()
            .setName(name)
            .setValue(value)
            .setIsString(isString)
            .build()

        then:
        argMeta.getName() == name
        argMeta.getValue() == value
        argMeta.getIsString() == isString

        where:
        name    | value | isString
        'name'  | 'abc' | true
        'name'  | 'abc' | false
    }
}
