package uapi.behavior;

/**
 * A IBehavior is responsible to process input event and output processed data based on specified event.
 *
 * @param   <I>
 *          Input event type
 * @param   <O>
 *          Output data type
 */
public interface IBehavior<I, O> extends IAction<I, O> {

    void setExecution(IExecution execution);
}
