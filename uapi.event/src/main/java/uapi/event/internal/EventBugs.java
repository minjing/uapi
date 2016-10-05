/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.event.internal;

import uapi.event.IEvent;
import uapi.event.IEventBus;
import uapi.event.IEventHandler;

/**
 * Created by min on 16/10/4.
 */
public class EventBugs implements IEventBus {
    @Override
    public void fire(IEvent event) {

    }

    @Override
    public void fire(IEvent event, boolean syncable) {

    }

    @Override
    public void register(IEventHandler eventHandler) {

    }
}
