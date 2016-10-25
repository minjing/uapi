package uapi.sample.behavior;

import uapi.event.IEvent;

/**
 * Created by xquan on 10/25/2016.
 */
public class CounterChangedEvent implements IEvent {

    public static final String EVENT_COUNTER_CHANGED   = "CounterChanged";

    private final int _counter;

    public CounterChangedEvent(int counter) {
        this._counter = counter;
    }

    @Override
    public String topic() {
        return EVENT_COUNTER_CHANGED;
    }

    public int counter() {
        return this._counter;
    }
}
