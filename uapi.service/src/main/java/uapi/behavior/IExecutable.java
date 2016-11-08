package uapi.behavior;

/**
 * The interface is used to generate IEventDrivenBehavior by EventBehavior annotation
 */
public interface IExecutable {

    /**
     * Return the root execution of the execution tree
     *
     * @return  The root execution
     */
    IExecution execution();
}
