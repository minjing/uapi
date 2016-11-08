package uapi.sample.behavior;

import uapi.behavior.IAction;
import uapi.behavior.IExecutionContext;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;

/**
 * Created by xquan on 11/8/2016.
 */
public class CounterChangedAction implements IAction<CounterChangedEvent, Void> {

    public static final String name = "CounterChangedAction";

    @Inject
    protected ILogger _logger;

    @Override
    public String name() {
        return name;
    }

    @Override
    public Void process(CounterChangedEvent event, IExecutionContext context) {
        this._logger.info("Handler event - {}", event.topic());
        this._logger.info("Counter change to - {}", event.counter());
        return null;
    }

    @Override
    public Class<CounterChangedEvent> inputType() {
        return CounterChangedEvent.class;
    }

    @Override
    public Class<Void> outputType() {
        return Void.class;
    }
}
