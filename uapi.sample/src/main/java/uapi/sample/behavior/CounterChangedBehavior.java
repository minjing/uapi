package uapi.sample.behavior;

import uapi.behavior.IExecutable;
import uapi.behavior.IExecution;
import uapi.behavior.IExecutionBuilderFactory;
import uapi.behavior.annotation.EventBehavior;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

@Service(ids=CounterChangedBehavior.name)
@EventBehavior(
        name=CounterChangedBehavior.name,
        topic=CounterChangedEvent.EVENT_COUNTER_CHANGED,
        event=CounterChangedEvent.class)
public class CounterChangedBehavior implements IExecutable {

    public static final String name = "NameChangedBehavior";

    @Inject
    IExecutionBuilderFactory _execBuilderFactory;

    @Override
    public IExecution execution() {
        return this._execBuilderFactory.from(CounterChangedAction.name).build();
    }
}
