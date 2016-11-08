/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.event;

/**
 * Event interface
 */
public interface IEvent {

    /**
     * Return event topic
     *
     * @return  The event topic
     */
    String topic();

    <T> void attach(String key, T data);

    <T> T attachment(String key);

    void clearAttachment(String key);

    void clearAttachments();
}
