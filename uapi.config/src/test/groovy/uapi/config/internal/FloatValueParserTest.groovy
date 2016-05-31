/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal

import spock.lang.Specification

/**
 * Test case for FloatValueParser
 */
class FloatValueParserTest extends Specification {

    def 'Test support types'() {
        given:
        FloatValueParser parser = new FloatValueParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                  | outType
        Float.canonicalName     | Float.canonicalName
        String.canonicalName    | Float.canonicalName
        String.canonicalName    | 'float'
    }

    def 'Test parse value'() {
        given:
        FloatValueParser parser = new FloatValueParser()

        expect:
        parser.parse(input) == output

        where:
        input       | output
        34.2f       | new Float(34.2f)
        "34.2"      | 34.2f
    }
}
