package uapi.behavior;

import uapi.event.IEvent;
import uapi.event.IEventHandler;

/**
 * A Behavior which is driven by specific event
 *
 * @param   <I>
 *          event type
 */
public interface IEventDrivenBehavior<I extends IEvent> extends IBehavior<I, Void>, IEventHandler<I> {

    /**
     * The default method just invoke event handling method and return nothing
     *
     * @param   input
     *          Inputted data
     * @return  nothing
     */
    @Override
    default Void process(I input, IExecutionContext context) {
        handle(input);
        return null;
    }
}
