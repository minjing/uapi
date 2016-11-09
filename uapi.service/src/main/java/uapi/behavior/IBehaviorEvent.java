package uapi.behavior;

import uapi.event.IEvent;

/**
 * Created by xquan on 11/9/2016.
 */
public interface IBehaviorEvent extends IEvent {

    <T> void attach(String key, T data);

    <T> T attachment(String key);

    void clearAttachment(String key);

    void clearAttachments();
}
