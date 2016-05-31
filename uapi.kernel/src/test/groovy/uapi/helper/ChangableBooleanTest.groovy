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

/**
 * Unit test for ChangableBoolean
 */
class ChangableBooleanTest extends Specification {

    def 'Test set value'() {
        given:
        ChangeableBoolean cb = new ChangeableBoolean()

        expect:
        ! cb.get()
        cb.set(true)
        cb.get()
    }
}
