package uapi.task;

/**
 * The interface will be used emit task to framework
 * 
 * @author min
 */
public interface ITaskEmitter {

    /**
     * Emit task to framework
     * 
     * @param task  The task which will be add to framework
     */
    void emit(ITask task);
}
