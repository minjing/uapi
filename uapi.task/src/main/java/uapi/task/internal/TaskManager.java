package uapi.task.internal;

import uapi.config.Config;
import uapi.log.ILogger;
import uapi.service.Inject;
import uapi.service.Registration;
import uapi.service.Type;
import uapi.task.INotifier;
import uapi.task.ITask;
import uapi.task.ITaskManager;
import uapi.task.ITaskProducer;

@Registration({
    @Type(TaskManager.class)
})
public final class TaskManager
    implements ITaskManager {

    @Inject
    private ILogger _logger;

    public TaskManager() {
    }

    public void setLogger(ILogger logger) {
        this._logger = logger;
    }

    @Config("uapi.task.worker_count")
    @Config("uapi.task.queue_size")
    public void config(String key, String config) {
        
    }

    @Override
    public void addTask(ITask task) {
//        if (task == null) {
//            throw new InvalidArgumentException("task", InvalidArgumentType.EMPTY);
//        }
//        this._logger.trace("Add a new task - {}", task.getDescription());
//        if (task instanceof ISerialTask) {
//            // Todo:
//        } else {
//            this._parallelQueue.add(task);
//        }
    }

    @Override
    public void addTask(ITask task, INotifier notifier) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void registerProducer(ITaskProducer producer) {
        // TODO Auto-generated method stub
        
    }
}
