/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal

import spock.lang.Specification
import uapi.config.IntervalTime

import java.util.concurrent.TimeUnit

/**
 * Unit test for {@code IntervalTimeParser}
 */
class IntervalTimeParserTest extends Specification {

    def 'Test support types'() {
        given:
        IntervalTimeParser parser = new IntervalTimeParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                              | outType
        String.class.getCanonicalName()     | IntervalTime.class.getCanonicalName()
    }

    def 'Test parse'() {
        given:
        IntervalTimeParser parser = new IntervalTimeParser()

        expect:
        parser.parse(input) == output

        where:
        input       | output
        '3m'        | new IntervalTime(3L, TimeUnit.MINUTES)
        '1h5m'      | new IntervalTime(65L, TimeUnit.MINUTES)
    }
}
