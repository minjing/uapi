package uapi.sample.behavior;

import uapi.behavior.IExecutable;
import uapi.behavior.IExecution;
import uapi.behavior.IExecutionBuilderFactory;
import uapi.behavior.annotation.EventBehavior;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

@Service(ids=CounterIncBehavior.name)
@EventBehavior(
        name=CounterIncBehavior.name,
        topic=CounterIncEvent.TOPIC,
        event=CounterIncEvent.class)
@Tag("Behavior Demo")
public class CounterIncBehavior implements IExecutable {

    public static final String name = "CounterIncBehavior";

    @Inject
    protected IExecutionBuilderFactory _execBuilderFactory;

    @Override
    public IExecution execution() {
        return this._execBuilderFactory.from(CounterIncAction.name).build();
    }
}
