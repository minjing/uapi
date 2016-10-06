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
 * A handler for specific event
 */
public interface IEventHandler {

    /**
     * The topic of event which can be handled by this handler
     *
     * @return  The event handler
     */
    String topic();

    /**
     * Handle event
     */
    void handle(IEvent event);
}
