package uapi.task.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.config.Config;
import uapi.log.ILogger;
import uapi.service.Inject;
import uapi.service.Registration;
import uapi.service.Type;
import uapi.task.ISerialTask;
import uapi.task.ITask;
import uapi.task.ITaskManager;

@Registration({
    @Type(TaskManager.class)
})
public final class TaskManager
    implements ITaskManager {

    @Inject
    private ILogger _logger;

    private final BlockingQueue<ITask> _parallelQueue;

    public TaskManager() {
        this._parallelQueue = new LinkedBlockingQueue<>();
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
        if (task == null) {
            throw new InvalidArgumentException("task", InvalidArgumentType.EMPTY);
        }
        if (task instanceof ISerialTask) {
            // Todo:
        } else {
            this._parallelQueue.add(task);
        }
    }

    private final class ParallelQueueExecutor {

    }
}
