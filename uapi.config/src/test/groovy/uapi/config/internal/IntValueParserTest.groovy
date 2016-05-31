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
 * Created by xquan on 4/7/2016.
 */
class IntValueParserTest extends Specification {

    def 'Test parser supported types'() {
        given:
        IntValueParser parser = new IntValueParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                  | outType
        String.canonicalName    | Integer.canonicalName
        String.canonicalName    | 'int'
        Integer.canonicalName   | Integer.canonicalName
        Integer.canonicalName   | 'int'
    }

    def 'Test parse string to int'() {
        given:
        IntValueParser parser = new IntValueParser()

        expect:
        parser.parse(input) == output

        where:
        input   | output
        '12'    | 12
        12      | 12
    }
}
