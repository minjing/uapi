package uapi.task.internal;

import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, SerialQueue> _serialQueueMap;

    public TaskManager() {
        this._parallelQueue = new LinkedBlockingQueue<>();
        this._serialQueueMap = new HashMap<>();
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
        this._logger.trace("Add a new task - {}", task.getDescription());
        if (task instanceof ISerialTask) {
            // Todo:
        } else {
            this._parallelQueue.add(task);
        }
    }

    private final class ParallelTaskExecutor {

    }

    private final class SerialTaskExecutor {
        
    }
}
