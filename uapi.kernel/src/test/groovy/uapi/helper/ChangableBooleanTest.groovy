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
