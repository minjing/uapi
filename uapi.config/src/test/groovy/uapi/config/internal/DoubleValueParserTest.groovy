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
 * Test case for DoubleValueParser
 */
class DoubleValueParserTest extends Specification {

    def 'Test supported types'() {
        given:
        DoubleValueParser parser = new DoubleValueParser()

        expect:
        parser.isSupport(intype, outtype)

        where:
        intype                  | outtype
        Double.canonicalName    | Double.canonicalName
        String.canonicalName    | Double.canonicalName
        String.canonicalName    | 'double'
        Double.canonicalName    | 'double'
    }

    def 'Test parse value'() {
        given:
        DoubleValueParser parser = new DoubleValueParser()

        expect:
        parser.parse(input) == output

        where:
        input               | output
        '12.3'              | 12.3d
        new Double(12.3d)   | 12.3d
        '12.3'              | new Double(12.3d)
    }
}
