package uapi.sample.behavior;

import uapi.behavior.IAction;
import uapi.behavior.IExecutionContext;
import uapi.behavior.annotation.Action;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

@Service(IAction.class)
@Action(CounterIncAction.name)
@Tag("Behavior Demo")
public abstract class CounterIncAction implements IAction<CounterIncEvent, Void> {

    public static final String name = "CounterIncAction";

    @Inject
    protected ILogger _logger;

//    @Inject
//    protected Store _store;

    @Override
    public String name() {
        return name;
    }

    @Override
    public Void process(CounterIncEvent event, IExecutionContext context) {
        this._logger.info("Handler event - {}", event.topic());
//        this._store.incCounter();
        return null;
    }
}
