package uapi.sample.behavior;

import uapi.event.IEvent;
import uapi.event.internal.PlainEvent;

/**
 * Created by xquan on 10/25/2016.
 */
public class CounterChangedEvent extends PlainEvent {

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
