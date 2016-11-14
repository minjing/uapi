package uapi.sample.behavior;

import uapi.behavior.*;
import uapi.behavior.annotation.EventBehavior;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

@Service(ids=CounterChangedBehavior.name)
@EventBehavior(
        name=CounterChangedBehavior.name,
        topic=CounterChangedEvent.EVENT_COUNTER_CHANGED,
        event=CounterChangedEvent.class)
@Tag("Behavior Demo")
public class CounterChangedBehavior implements IExecutable {

    public static final String name = "NameChangedBehavior";

    @Inject
    IExecutionBuilderFactory _execBuilderFactory;

    @Override
    public IExecution execution() {
        return this._execBuilderFactory.from(CounterChangedAction.name).build();
    }
}
