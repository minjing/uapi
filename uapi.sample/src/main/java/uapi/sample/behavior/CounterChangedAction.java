package uapi.sample.behavior;

import uapi.behavior.IAction;
import uapi.behavior.IExecutionContext;
import uapi.behavior.annotation.Action;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * CounterChangedAction
 */
@Service(IAction.class)
@Action(CounterChangedAction.name)
public abstract class CounterChangedAction implements IAction<CounterChangedEvent, Void> {

    public static final String name = "CounterChangedAction";

    @Inject
    protected ILogger _logger;

    @Override
    public Void process(CounterChangedEvent event, IExecutionContext context) {
        this._logger.info("Handler event - {}", event.topic());
        this._logger.info("Counter change to - {}", event.counter());
        return null;
    }
}
