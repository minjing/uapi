package uapi.sample.behavior;

import uapi.behavior.IEventDrivenBehavior;
import uapi.behavior.IExecution;
import uapi.behavior.IExecutionContext;
import uapi.behavior.IResponsible;
import uapi.event.IEvent;
import uapi.event.IEventBus;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 10/25/2016.
 */
@Service(IResponsible.class)
public class Store implements IResponsible {

    public static final String EVENT_COUNTER_CHANGED   = "CounterChanged";

    private int _counter;

    @Inject
    protected IEventBus _eventBus;

    @Inject
    protected ILogger _logger;

    @Override
    public String name() {
        return "Store";
    }

    @Override
    public IEventDrivenBehavior[] behaviors() {
        return null;
    }

    private class IncCounterHandler implements IEventDrivenBehavior<IEvent> {

        @Override
        public String name() {
            return "IncCounterHandler";
        }

        @Override
        public Void process(IEvent input, IExecutionContext context) {
            return null;
        }

        @Override
        public Class<IEvent> inputType() {
            return IEvent.class;
        }

        @Override
        public Class<Void> outputType() {
            return Void.class;
        }

        @Override
        public String topic() {
            return View.EVENT_INC_COUNTER;
        }

        @Override
        public void handle(IEvent event) {
            Store.this._logger.info("Handler event - {}", event.topic());
            Store.this._counter++;
            Store.this._eventBus.fire(EVENT_COUNTER_CHANGED);
        }

        @Override
        public void setExecution(IExecution execution) {

        }
    }
}
