package uapi.behavior;

import uapi.event.IEvent;

/**
 * The event for Behavior only
 */
public interface IBehaviorEvent extends IEvent {

    /**
     * Attach a data for specific key
     *
     * @param   key
     *          The key
     * @param   data
     *          Attached data
     * @param   <T>
     *          The data type
     */
    <T> void attach(String key, T data);

    /**
     * Get attached data by specific key
     *
     * @param   key
     *          The key
     * @param   <T>
     *          The data type
     * @return  The data or null if no data was found
     */
    <T> T attachment(String key);

    /**
     * Clear data by specific key
     *
     * @param   key
     *          The key
     */
    void clearAttachment(String key);

    /**
     * Clear all data in this event
     */
    void clearAttachments();
}
