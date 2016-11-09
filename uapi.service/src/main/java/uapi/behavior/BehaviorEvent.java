package uapi.behavior;

import uapi.event.PlainEvent;
import uapi.helper.ArgumentChecker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by min on 2016/11/5.
 */
public class BehaviorEvent extends PlainEvent implements IBehaviorEvent {
    private final Map<String, Object> _attachments;

    public BehaviorEvent(final String topic) {
        super(topic);
        this._attachments = new HashMap<>();
    }

    @Override
    public <T> void attach(String key, T data) {
        ArgumentChecker.required(key, "key");
        ArgumentChecker.required(data, "data");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T attachment(String key) {
        ArgumentChecker.required(key, "key");
        return (T) this._attachments.get(key);
    }

    @Override
    public void clearAttachment(String key) {
        ArgumentChecker.required(key, "key");
        this._attachments.remove(key);
    }

    @Override
    public void clearAttachments() {
        this._attachments.clear();
    }
}
