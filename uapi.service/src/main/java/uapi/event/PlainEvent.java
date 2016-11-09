package uapi.event;

import uapi.helper.ArgumentChecker;

import java.util.HashMap;
import java.util.Map;

/**
 * Most simple event which only contains event topic
 */
public class PlainEvent implements IEvent {

    private final String _topic;

    private final Map<String, Object> _attachments;

    public PlainEvent(final String topic) {
        ArgumentChecker.required(topic, "topic");
        this._topic = topic;
        this._attachments = new HashMap<>();
    }

    @Override
    public String topic() {
        return this._topic;
    }

//    @Override
//    public <T> void attach(String key, T data) {
//        ArgumentChecker.required(key, "key");
//        ArgumentChecker.required(data, "data");
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> T attachment(String key) {
//        ArgumentChecker.required(key, "key");
//        return (T) this._attachments.get(key);
//    }
//
//    @Override
//    public void clearAttachment(String key) {
//        ArgumentChecker.required(key, "key");
//        this._attachments.remove(key);
//    }
//
//    @Override
//    public void clearAttachments() {
//        this._attachments.clear();
//    }
}
