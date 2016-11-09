package uapi.sample.behavior;

import uapi.behavior.*;
import uapi.event.IEventBus;
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

    @Inject(CounterChangedBehavior.name)
    protected IEventDrivenBehavior _counterChangedbehavior;

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
                this._counterChangedbehavior
        };
    }
}
