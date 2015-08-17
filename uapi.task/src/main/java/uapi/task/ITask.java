package uapi.task;

/**
 * A task is an abstract executable unit
 * 
 * @author min
 */
public interface ITask {

    /**
     * The task entry point
     */
    void run();

    /**
     * Return the priority of this task
     * The priority value must be between 0 to 128
     * 
     * @return  The priority
     */
    int getPriority();

    /**
     * Return the description of the task
     * 
     * @return  Task description
     */
    String getDescription();
}
