package uapi.behavior.internal;

import uapi.event.IEvent;
import uapi.helper.ArgumentChecker;

import java.util.Map;

/**
 * Created by min on 2016/11/5.
 */
public class BehaviorEvent implements IEvent {

    private final IEvent _event;
    private final Map _data;

    BehaviorEvent(final IEvent event, final Map data) {
        ArgumentChecker.required(event, "event");
        ArgumentChecker.required(data, "data");

        this._event = event;
        this._data = data;
    }

    @Override
    public String topic() {
        return this._event.topic();
    }

    @Override
    public <T> void attach(String key, T data) {

    }

    @Override
    public <T> T attachment(String key) {
        return null;
    }

    @Override
    public void clearAttachment(String key) {

    }

    @Override
    public void clearAttachments() {

    }

    IEvent rawEvent() {
        return this._event;
    }

    Map data() {
        return this._data;
    }
}
