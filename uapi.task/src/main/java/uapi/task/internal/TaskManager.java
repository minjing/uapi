package uapi.task.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import uapi.IStateWatcher;
import uapi.IStateful;
import uapi.config.Config;
import uapi.helper.ArgumentChecker;
import uapi.log.ILogger;
import uapi.service.IService;
import uapi.service.Inject;
import uapi.service.Registration;
import uapi.service.Type;
import uapi.task.INotifier;
import uapi.task.ISerialTask;
import uapi.task.ITask;
import uapi.task.ITaskManager;
import uapi.task.ITaskProducer;

@Registration({
    @Type(TaskManager.class)
})
public final class TaskManager
    implements ITaskManager, IService {

    @Inject
    private ILogger _logger;

    @Inject
    private TaskTransfer _taskTransfer;

    private final List<ITaskProducer> _taskProducers;
    private final TaskConverter _taskConverter;
    private final Map<String, PriorityBlockingQueue<SerialTaskInfo>> _serialQueues;

    public TaskManager() {
        this._taskProducers = new ArrayList<>();
        this._taskConverter = new TaskConverter();
        this._serialQueues = new ConcurrentHashMap<>();
    }

    public void setLogger(ILogger logger) {
        this._logger = logger;
    }

    public void setTaskTransfer(TaskTransfer transfer) {
        this._taskTransfer = transfer;
    }

    @Config("uapi.task.worker_count")
    @Config("uapi.task.queue_size")
    public void config(String key, String config) {
        
    }

    @Override
    public void addTask(ITask task) {
        addTask(task, null);
    }

    @Override
    public void addTask(ITask task, INotifier notifier) {
        if (task instanceof ISerialTask) {
            addSerialTask((ISerialTask) task, notifier);
        } else {
            addNormalTask(task, notifier);
        }
    }

    @Override
    public void registerProducer(ITaskProducer producer) {
        TaskEmitter taskEmitter = new TaskEmitter(this._taskConverter);
        producer.setEmitter(taskEmitter);
        this._taskProducers.add(producer);
        this._taskTransfer.addTaskEmitter(taskEmitter);
    }

    private void addNormalTask(ITask task, INotifier notifier) {
        ITask wrappedTask = this._taskConverter.convert(task, notifier);
        this._taskTransfer.transferTask(wrappedTask);
    }

    private void addSerialTask(ISerialTask task, INotifier notifier) {
        SerialTaskInfo taskInfo = new SerialTaskInfo(task, notifier);
        String serialId = task.getSerialId();
        PriorityBlockingQueue<SerialTaskInfo> taskInfos = 
                this._serialQueues.putIfAbsent(serialId, new PriorityBlockingQueue<>());
        taskInfos.add(taskInfo);
    }

    private static final class SerialTaskInfo {

        private final ISerialTask _task;
        private final INotifier _notifier;

        private SerialTaskInfo(ISerialTask task, INotifier notifier) {
            this._task = task;
            this._notifier = notifier;
        }
    }

    private final class CheckSerialQueueTask implements Runnable {

        @Override
        public void run() {
            TaskManager.this._serialQueues.forEach((id, taskQueue) -> {
                // TODO: 
            });
        }
    }

    private static final class NotifyStateTask implements ITask {

        private final ITask     _task;
        private final INotifier _notifier;
        private final int       _newState;
        private final Throwable _throwable;

        private NotifyStateTask(ITask task, INotifier notifier, int newState) {
            this(task, notifier, newState, null);
        }

        private NotifyStateTask
        (ITask task, INotifier notifier, int newState, Throwable throwable) {
            this._task      = task;
            this._notifier  = notifier;
            this._newState  = newState;
            this._throwable = throwable;
        }

        @Override
        public void run() {
            if (this._throwable != null) {
                this._notifier.onFailed(this._task, this._throwable);
                return;
            }
            if (this._newState == IStateful.STATE_TERMINAL) {
                this._notifier.onDone(this._task);
                return;
            }
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public String getDescription() {
            return "Notify state task";
        }
    }

    private final class TaskStateWatcher
        implements IStateWatcher {

        private final ITask _task;
        private final INotifier _notifier;

        private TaskStateWatcher(final ITask task, final INotifier notifier) {
            ArgumentChecker.notNull(task, "task");
            ArgumentChecker.notNull(notifier, "notifier");
            this._task = task;
            this._notifier = notifier;
        }

        @Override
        public void stateChanged(IStateful which, int oldState, int newState) {
            ArgumentChecker.notNull(which, "which");
            TaskManager.this._taskTransfer.transferTask(
                    new NotifyStateTask(this._task, this._notifier, newState));
        }

        @Override
        public void stateChange(IStateful which, int oldState, int newState, Throwable t) {
            ArgumentChecker.notNull(which, "which");
            TaskManager.this._taskTransfer.transferTask(
                    new NotifyStateTask(this._task, this._notifier, newState, t));
        }
    }

    final class TaskConverter {

        ITask convert(ITask task, INotifier notifier) {
            ArgumentChecker.notNull(task, "task");
            TaskManager.this._logger.trace("Add a new task - {}", task.getDescription());

            // We need cover two cases:
            // 1. user want to get the notify of task progress
            // 2. user don't care about task progress
            if (notifier == null) {
                return task;
            } else {
                TaskStateWatcher taskWatcher = new TaskStateWatcher(task, notifier);
                StatefulTask statefulTask = new StatefulTask(task);
                statefulTask.setWatcher(taskWatcher);
                return statefulTask;
            }
        }
    }
}
