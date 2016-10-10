package uapi.sample.event;

import uapi.event.IEventBus;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * The class rise a event
 */
@Service
@Tag("Event Demo")
public class EventSource {

    @Inject
    protected IEventBus _eventBus;

    public void riseEvent() {
        this._eventBus.fire(new MyEvent("new event"));
    }
}
