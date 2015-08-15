package uapi.task;

/**
 * A notifier is used for task status notifying when the task
 * is done or failed by framework
 * 
 * @author min
 */
public interface INotifier {

    /**
     * Invoked when the task is done
     * 
     * @param task  The task which is done by framework
     */
    void onDone(ITask task);

    /**
     * Invoked when processing the task failed
     * 
     * @param task  The task which is processed failed by framework
     * @param t     The exception object if it has
     */
    void onFailed(ITask task, Throwable t);
}
