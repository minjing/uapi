package uapi.sample.behavior;

import uapi.behavior.*;
import uapi.event.IEventBus;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * Created by xquan on 10/25/2016.
 */
@Service(IResponsible.class)
@Tag("Behavior Demo")
public class View implements IResponsible {

    @Inject
    protected IEventBus _eventBus;

    @Inject(CounterChangedBehavior.name)
    protected IEventDrivenBehavior _counterChangedbehavior;

    @Override
    public String name() {
        return "View";
    }

    public void incCounter() {
        this._eventBus.fire(CounterIncEvent.TOPIC);
    }

    @Override
    public IEventDrivenBehavior[] behaviors() {
        return new IEventDrivenBehavior[] {
                this._counterChangedbehavior
        };
    }
}
