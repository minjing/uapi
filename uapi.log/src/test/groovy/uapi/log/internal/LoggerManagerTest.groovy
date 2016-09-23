/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.log.internal

import spock.lang.Specification
import uapi.InvalidArgumentException

/**
 * Unit test for LoggerManager
 */
class LoggerManagerTest extends Specification {

    def 'Test createService'() {
        given:
        LoggerManager logMgr = new LoggerManager()

        expect:
        logMgr.createService(new Object()) != null
    }

    def 'Test createService without objectFor'() {
        given:
        LoggerManager logMgr = new LoggerManager()

        when:
        logMgr.createService(null)

        then:
        thrown(InvalidArgumentException)
    }
}
