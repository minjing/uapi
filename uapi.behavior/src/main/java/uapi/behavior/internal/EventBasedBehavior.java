package uapi.behavior.internal;

import uapi.behavior.IEventDrivenBehavior;
import uapi.behavior.IExecution;
import uapi.behavior.IExecutionContext;
import uapi.behavior.Scope;
import uapi.event.IEvent;
import uapi.event.IEventBus;
import uapi.helper.ArgumentChecker;

/**
 * The behavior which is triggered by event
 */
public class EventBasedBehavior implements IEventDrivenBehavior<IEvent> {

    private final IEventBus _eventBus;
    private final String _name;

    private IExecution _execution;

    EventBasedBehavior(final String name, final IEventBus eventBus) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(eventBus, "eventBus");

        this._name = name;
        this._eventBus = eventBus;
    }

    @Override
    public String name() {
        return this._name;
    }

    @Override
    public String topic() {
        return null;
    }

    @Override
    public Class inputType() {
        return null;
    }

    @Override
    public void setExecution(IExecution execution) {
        ArgumentChecker.required(execution, "execution");

        this._execution = execution;
    }

    @Override
    public Void process(IEvent input, IExecutionContext context) {
        IExecution execution = this._execution;
        while (execution != null) {
            Object result = execution.execute(input, context);
            execution = execution.next(result);
        }

        return null;
    }

    @Override
    public void handle(IEvent event) {
        ExecutionContext context = new ExecutionContext(this._eventBus);
        IEvent rawEvent = event;
        if (event instanceof BehaviorEvent) {
            BehaviorEvent bEvent = (BehaviorEvent) event;
            context.put(bEvent.data(), Scope.Global);
            rawEvent = bEvent.rawEvent();
        }
        process(rawEvent, context);
    }
}
