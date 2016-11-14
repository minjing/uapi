package uapi.sample.behavior;

import uapi.behavior.*;
import uapi.event.IEvent;
import uapi.event.IEventBus;
import uapi.log.ILogger;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * Created by xquan on 10/25/2016.
 */
@Service(IResponsible.class)
@Tag("Behavior Demo")
public class Store implements IResponsible {

    private int _counter;

    @Inject
    protected IBehaviorRepository _behaviorRepo;

    @Inject
    protected IEventBus _eventBus;

    @Inject
    protected ILogger _logger;

    @Inject(CounterIncBehavior.name)
    protected IEventDrivenBehavior _counterIncBehavior;

//    @Init
//    public void init() {
//        this._behaviorRepo.register(new CounterIncAction());
//    }

    @Override
    public String name() {
        return "Store";
    }

    @Override
    public IEventDrivenBehavior[] behaviors() {
        return new IEventDrivenBehavior[] {
                this._counterIncBehavior
        };
    }
}
