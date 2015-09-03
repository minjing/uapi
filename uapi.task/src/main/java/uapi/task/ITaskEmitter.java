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
     * @param   task
     *          The task which will be add to framework
     */
    void emit(ITask task);

    /**
     * Emit task with notifier to framework
     * 
     * @param   task
     *          The task which will be emitted
     * @param   notifier
     *          The notifier which will be invoked when specified event happened
     */
    void emit(ITask task, INotifier notifier);
}
