package uapi.sample.event;

import uapi.event.IEvent;

/**
 * Customer event class
 */
public class MyEvent implements IEvent {

    public static final String TOPIC = "MyTopic";

    private final String _name;

    public MyEvent(String name) {
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
