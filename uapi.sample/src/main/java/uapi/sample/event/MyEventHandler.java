package uapi.sample.event;

import uapi.event.IEvent;
import uapi.event.IEventHandler;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * A event handler
 */
@Service(IEventHandler.class)
@Tag("Event Demo")
public class MyEventHandler implements IEventHandler {

    @Inject
    protected ILogger _logger;

    @Override
    public String topic() {
        return MyEvent.TOPIC;
    }

    @Override
    public void handle(IEvent event) {
        this._logger.info("Processing a new event...");
        assert event != null;
        assert event instanceof MyEvent;
        assert ((MyEvent) event).name().equals("new event");
    }
}
