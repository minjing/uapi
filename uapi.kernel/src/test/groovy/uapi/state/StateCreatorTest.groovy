package uapi.state

import spock.lang.Specification

/**
 * Unit test for StateCreator
 */
class StateCreatorTest extends Specification {

    def 'test create'() {
        given:
        def shifter = Mock(IShifter)

        when:
        def state = StateCreator.createTracer(shifter, "init")

        then:
        state != null
        state.get() == "init"
    }
}
