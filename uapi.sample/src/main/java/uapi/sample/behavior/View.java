package uapi.sample.behavior;

import uapi.behavior.IEventDrivenBehavior;
import uapi.behavior.IResponsible;
import uapi.event.IEventBus;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 10/25/2016.
 */
@Service(IResponsible.class)
public class View implements IResponsible {

    public static final String EVENT_INC_COUNTER  = "IncCounter";

    @Inject
    protected IEventBus _eventBus;

    @Inject
    protected ILogger _logger;

    @Override
    public String name() {
        return "View";
    }

    public void incCounter() {
        this._eventBus.fire(EVENT_INC_COUNTER);
    }

    @Override
    public IEventDrivenBehavior[] behaviors() {
        return new IEventDrivenBehavior[] {
                new CounterChangedHandler()
        };
    }

    private class CounterChangedHandler implements IEventDrivenBehavior<CounterChangedEvent> {

        @Override
        public String name() {
            return "NameChangedHandler";
        }

        @Override
        public Class<CounterChangedEvent> inputType() {
            return CounterChangedEvent.class;
        }

        @Override
        public Class<Void> outputType() {
            return Void.class;
        }

        @Override
        public String topic() {
            return CounterChangedEvent.EVENT_COUNTER_CHANGED;
        }

        @Override
        public void handle(CounterChangedEvent event) {
            View.this._logger.info("Handler event - {}", event.topic());
            View.this._logger.info("Counter change to - {}", event.counter());
        }
    }
}
