package uapi.task;

/**
 * The task manager is entry point of whole task framework
 * It can add task directly or register new task producer outside
 * 
 * @author min
 */
public interface ITaskManager {

    /**
     * Add new task
     * 
     * @param task  The task which will be added
     */
    void addTask(ITask task);

    /**
     * Add new task and a notifier which will be used when task is done or failed
     * 
     * @param task      The new task which will be added
     * @param notifier  The associated notified which will be used when task is done or failed
     */
    void addTask(ITask task, INotifier notifier);
    
    /**
     * Register a new task producer which will generate new task
     * 
     * @param producer  The task producer which will generate new task
     */
    void registerProducer(ITaskProducer producer);
}
