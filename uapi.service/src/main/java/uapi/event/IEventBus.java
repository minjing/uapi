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
 * A event bus is used to dispatch event
 */
public interface IEventBus {

    /**
     * Fire event
     *
     * @param   event
     *          Fired event
     */
    void fire(IEvent event);

    /**
     * Fire event which
     *
     * @param   event
     *          Fired event
     * @param   syncable
     *          Synchronous or asynchronous to fire
     */
    void fire(IEvent event, boolean syncable);

    /**
     * Register a event handler
     *
     * @param   eventHandler
     *          The event handler
     */
    void register(IEventHandler eventHandler);
}
