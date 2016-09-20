/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config

import spock.lang.Specification
import uapi.InvalidArgumentException

/**
 * Unit test for IntervalTime
 */
class IntervalTimeTest extends Specification {

    def 'Test parse and toString'() {
        when:
        def intervalTime = IntervalTime.parse(timeString)

        then:
        intervalTime.days()         == days
        intervalTime.hours()        == hours
        intervalTime.minutes()      == minutes
        intervalTime.seconds()      == seconds
        intervalTime.milliseconds() == milliseconds
        intervalTime.toString()     == timeString

        where:
        timeString  | days  | hours | minutes       | seconds               | milliseconds
        '50ms'      | 0     | 0     | 0             | 0                     | 50
        '2s'        | 0     | 0     | 0             | 2                     | 2000
        '2s30ms'    | 0     | 0     | 0             | 2                     | 2030
        '2s900ms'   | 0     | 0     | 0             | 2                     | 2900
        '1m'        | 0     | 0     | 1             | 60                    | 60 * 1000
        '1m50s'     | 0     | 0     | 1             | 110                   | 110 * 1000
        '2m30s20ms' | 0     | 0     | 2             | 150                   | 150 * 1000 + 20
        '3h'        | 0     | 3     | 180           | 180 * 60              | 180 * 60 * 1000
        '3h5s'      | 0     | 3     | 180           | 180 * 60 + 5          | (180 * 60 + 5) * 1000
        '1d5m'      | 1     | 24    | 24 * 60 + 5   | (24 * 60 + 5) * 60    | (24 * 60 + 5) * 60 * 1000
    }

    def 'Test parse with incorrect unit'() {
        when:
        IntervalTime.parse(timeString)

        then:
        thrown(ex)

        where:
        timeString  | ex
        ''          | InvalidArgumentException
        null        | InvalidArgumentException
        '1b'        | InvalidArgumentException
        '1m3'       | InvalidArgumentException
        'm23'       | InvalidArgumentException
        '1.5m'      | InvalidArgumentException
    }
}
