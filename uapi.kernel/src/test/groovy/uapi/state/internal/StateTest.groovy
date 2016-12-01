package uapi.state.internal

import junit.extensions.TestSetup
import spock.lang.Specification
import uapi.KernelException
import uapi.state.IOperation
import uapi.state.IShifter
import uapi.state.IState
import uapi.state.IStateListener

/**
 * Unit test for State
 */
class StateTest extends Specification {

    public static final STATE_ACTIVE   = "active"
    public static final STATE_DEACTIVE = "deactive";

    def testShift() {
        given:
        IState<TestSetup> state = new State(new Shifter(), TestState.INIT)

        when:
        state.shift(toState)

        then:
        state.get() == expectedState

        where:
        toState         | expectedState
        STATE_ACTIVE    | TestState.ACTIVED
    }

    def testSubscribe() {
        given:
        IState<TestSetup> state = new State(new Shifter(), TestState.INIT)
        def listener = Mock(IStateListener)
        state.subscribe(listener)

        when:
        state.shift(toState)

        then:
        state.get() == expectedState
        1 * listener.stateChanged(TestState.INIT, TestState.ACTIVED)

        where:
        toState         | expectedState
        STATE_ACTIVE    | TestState.ACTIVED
    }
}

class Shifter implements IShifter<TestState> {

    @Override
    TestState shift(TestState state, IOperation operation) {
        switch (operation.type()) {
            case StateTest.STATE_ACTIVE:
                return TestState.ACTIVED
            case StateTest.STATE_DEACTIVE:
                return TestState.DEACTIVED
            default:
                throw new KernelException("unsupported state");
        }
    }
}

enum TestState {
    INIT, ACTIVED, DEACTIVED
}