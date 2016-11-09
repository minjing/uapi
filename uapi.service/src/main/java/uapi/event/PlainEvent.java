package uapi.event;

import uapi.helper.ArgumentChecker;

/**
 * Most simple event which only contains event topic
 */
public class PlainEvent implements IEvent {

    private final String _topic;

    public PlainEvent(final String topic) {
        ArgumentChecker.required(topic, "topic");
        this._topic = topic;
    }

    @Override
    public String topic() {
        return this._topic;
    }
}
