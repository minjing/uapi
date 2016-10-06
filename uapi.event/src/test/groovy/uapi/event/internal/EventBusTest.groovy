/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.event.internal

import spock.lang.Specification
import uapi.event.IEvent
import uapi.event.IEventHandler
import uapi.event.NoEventHandlerException

class EventBusTest extends Specification{

    def 'Test fire when no event handler'() {
        given:
        IEvent event = Mock(IEvent) {
            topic() >> eventTopic
        }
        EventBus eventBus = new EventBus()
        eventBus.init()

        when:
        eventBus.fire(event)
        eventBus.destroy()

        then:
        thrown(NoEventHandlerException)

        where:
        eventTopic  | none
        'Topic'     | null
    }

    def 'Test fire'() {
        given:
        IEvent event = Mock(IEvent) {
            topic() >> eventTopic
        }
        IEventHandler handler = Mock(IEventHandler) {
            topic() >> eventTopic
        }
        EventBus eventBus = new EventBus()
        eventBus.init()
        eventBus.register(handler)

        when:
        eventBus.fire(event)
        eventBus.destroy()

        then:
        noExceptionThrown()
        1 * handler.handle(event)

        where:
        eventTopic  | none
        'Topic'     | null
    }

    def 'Test fire event with sync option'() {
        given:
        IEvent event = Mock(IEvent) {
            topic() >> eventTopic
        }
        IEventHandler handler = Mock(IEventHandler) {
            topic() >> eventTopic
        }
        EventBus eventBus = new EventBus()
        eventBus.init()
        eventBus.register(handler)

        when:
        eventBus.fire(event, true)

        then:
        noExceptionThrown()
        1 * handler.handle(event)

        where:
        eventTopic  | none
        'Topic'     | null
    }
}
