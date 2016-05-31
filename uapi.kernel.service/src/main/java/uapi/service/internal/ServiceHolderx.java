/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xquan on 4/19/2016.
 */
@Deprecated
public class ServiceHolderx {

    Transitional tryResolve = () -> ServiceState.Resolved;

    public void init() {
        StateMachine sm = new StateMachine();
        sm.define(State.Initial, tryResolve, false, ServiceState.Resolved);
    }

    @FunctionalInterface
    interface Transitional {
        int transit();
    }

    static class State {
        static final int Initial    = 0;
        static final int Terminate  = -1;
        static final int ING        = -2;
    }

    static class ServiceState extends State {
        static final int Resolving  = 1;
        static final int Resolved   = 2;
        static final int Injecting  = 3;
        static final int Injected   = 4;
        static final int Statisfing = 5;
        static final int Statisfied = 6;
        static final int Initing    = 7;
        static final int Inited     = 8;
    }

    private static final class StateMachine {

        private int _state = State.Initial;
        private Lock _lock = new ReentrantLock();
        private List<Transition> _transitions = new LinkedList<>();

        public int getState() {
            return this._state;
        }

        public void define(int fromState, Transitional trans, boolean failedToTerminate, int... toStates) {

        }
    }

    class Transition {
        int _fromState;
        Transitional _transitional;
        boolean _failedToTerminate;
        int[] _toStates = new int[10];

        Transition from(int state) {
            this._fromState = state;
            return this;
        }

        Transition execute(Transitional transitional) {
            this._transitional = transitional;
            return this;
        }
    }
}
