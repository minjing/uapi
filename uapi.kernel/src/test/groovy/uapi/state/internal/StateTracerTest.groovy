package uapi.state.internal

import junit.extensions.TestSetup
import spock.lang.Specification
import uapi.KernelException
import uapi.state.IOperation
import uapi.state.IShifter
import uapi.state.IStateListener
import uapi.state.IStateTracer

/**
 * Unit test for StateTracer
 */
class StateTracerTest extends Specification {

    public static final STATE_ACTIVE   = "active"
    public static final STATE_DEACTIVE = "deactive";

    def testShift() {
        given:
        IStateTracer<TestSetup> state = new StateTracer(new Shifter(), TestState.INIT)

        when:
        state.shift(toState)

        then:
        state.get() == expectedState

        where:
        toState         | expectedState
        STATE_ACTIVE    | TestState.ACTIVED
    }

    def testShiftUnsupportedOperation() {
        given:
        IStateTracer<TestSetup> state = new StateTracer(new Shifter(), TestState.INIT)

        when:
        state.shift(toState)

        then:
        thrown(KernelException)

        where:
        toState | unsupported
        'ABC'   | 'ABC'
    }

    def testSubscribe() {
        given:
        IStateTracer<TestSetup> state = new StateTracer(new Shifter(), TestState.INIT)
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

    def testDuplicatedSbuscribe() {
        given:
        IStateTracer<TestSetup> state = new StateTracer(new Shifter(), TestState.INIT)
        def listener = Mock(IStateListener)
        state.subscribe(listener)

        when:
        state.subscribe(listener)

        then:
        thrown(KernelException)
    }

    def testUnsubscribe() {
        given:
        IStateTracer<TestSetup> state = new StateTracer(new Shifter(), TestState.INIT)
        def listener = Mock(IStateListener)
        state.subscribe(listener)

        when:
        state.unsubscribe(listener)
        state.shift(toState)

        then:
        state.get() == expectedState
        0 * listener.stateChanged(TestState.INIT, TestState.ACTIVED)

        where:
        toState         | expectedState
        STATE_ACTIVE    | TestState.ACTIVED
    }
}

class Shifter implements IShifter<TestState> {

    @Override
    TestState shift(TestState state, IOperation operation) {
        switch (operation.type()) {
            case StateTracerTest.STATE_ACTIVE:
                return TestState.ACTIVED
            case StateTracerTest.STATE_DEACTIVE:
                return TestState.DEACTIVED
            default:
                throw new KernelException("unsupported state");
        }
    }
}

enum TestState {
    INIT, ACTIVED, DEACTIVED
}