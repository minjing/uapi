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
     * Fire a event which only contains topic, the event will be handled in async
     *
     * @param   topic
     *          The event topic
     * @throws  NoEventHandlerException
     *          No event handler can handle the event
     */
    void fire(String topic) throws NoEventHandlerException;

    /**
     * Fire a event which only contains topic, the event will be handled by syncable tag
     *
     * @param   topic
     *          The event topic
     * @param   syncable
     *          Synchronous or asynchronous to fire
     * @throws  NoEventHandlerException
     *          No event handler can handle the event
     */
    void fire(String topic, boolean syncable) throws  NoEventHandlerException;

    /**
     * Fire event, the event will be handled in async
     *
     * @param   event
     *          Fired event
     * @throws  NoEventHandlerException
     *          No event handler can handle the event
     */
    void fire(IEvent event) throws NoEventHandlerException;

    /**
     * Fire event which
     *
     * @param   event
     *          Fired event
     * @param   syncable
     *          Synchronous or asynchronous to fire
     * @throws  NoEventHandlerException
     *          No event handler can handle the event
     */
    void fire(IEvent event, boolean syncable) throws NoEventHandlerException;

    /**
     * Register a event handler
     *
     * @param   eventHandler
     *          The event handler
     */
    void register(IEventHandler eventHandler);
}
