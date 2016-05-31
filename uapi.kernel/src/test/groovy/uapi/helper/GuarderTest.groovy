/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper

import spock.lang.Specification

import java.util.concurrent.locks.Lock

/**
 * Unit test for Guarder
 */
class GuarderTest extends Specification {

    def 'Test run'() {
        given:
        Lock lock = Mock(Lock)
        final List<String> strs = new ArrayList<>()

        when:
        Guarder.by(lock).run({ strs.add("Hello"); })

        then:
        strs.size() == 1
        strs.get(0) == "Hello"
        1 * lock.lock()
        1 * lock.unlock()
    }

    def 'Test run by result'() {
        given:
        Lock lock = Mock(Lock)

        when:
        String result = Guarder.by(lock).runForResult({ return "Hello"; })

        then:
        result == "Hello"
        1 * lock.lock()
        1 * lock.unlock()
    }
}
