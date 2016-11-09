package uapi.sample.behavior;

import uapi.behavior.BehaviorEvent;

/**
 * Created by xquan on 10/25/2016.
 */
public class CounterChangedEvent extends BehaviorEvent {

    public static final String EVENT_COUNTER_CHANGED   = "CounterChanged";

    private final int _counter;

    public CounterChangedEvent(int counter) {
        super(EVENT_COUNTER_CHANGED);
        this._counter = counter;
    }

    public int counter() {
        return this._counter;
    }
}
