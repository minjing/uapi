package uapi.sample.event;

import uapi.event.IEvent;
import uapi.event.internal.PlainEvent;

/**
 * Customer event class
 */
public class MyEvent extends PlainEvent {

    public static final String TOPIC = "MyTopic";

    private final String _name;

    public MyEvent(String name) {
        super(TOPIC);
        this._name = name;
    }

    @Override
    public String topic() {
        return TOPIC;
    }

    public String name() {
        return this._name;
    }
}
