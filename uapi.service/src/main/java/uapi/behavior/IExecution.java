package uapi.behavior;

/**
 * Present an action execution
 */
public interface IExecution {

    /**
     * Execute the action by specific data and context
     *
     * @param   data
     *          The input data
     * @param   context
     *          The execution context
     */
    void execute(Object data, IExecutionContext context);
}
