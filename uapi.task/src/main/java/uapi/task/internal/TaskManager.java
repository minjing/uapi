package uapi.task.internal;

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

    public TaskManager() { }

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
        ArgumentChecker.isEmpty(task, "task");
        this._logger.trace("Add a new task - {}", task.getDescription());

        // We need cover two cases:
        // 1. user want to get the notify of task progress
        // 2. user don't care about task progress
        if (notifier == null) {
            this._taskTransfer.transferTask(task);
        } else {
            TaskStateWatcher taskWatcher = new TaskStateWatcher(task, notifier);
            StatefulTask statefulTask = new StatefulTask();
            statefulTask.setWatcher(taskWatcher);
            this._taskTransfer.transferTask(statefulTask);
        }
    }

    @Override
    public void registerProducer(ITaskProducer producer) {
        // TODO Auto-generated method stub
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
            ArgumentChecker.isEmpty(task, "task");
            ArgumentChecker.isEmpty(notifier, "notifier");
            this._task = task;
            this._notifier = notifier;
        }

        @Override
        public void stateChanged(IStateful which, int oldState, int newState) {
            ArgumentChecker.isEmpty(which, "which");
            TaskManager.this._taskTransfer.transferTask(
                    new NotifyStateTask(this._task, this._notifier, newState));
        }

        @Override
        public void stateChange(IStateful which, int oldState, int newState, Throwable t) {
            ArgumentChecker.isEmpty(which, "which");
            TaskManager.this._taskTransfer.transferTask(
                    new NotifyStateTask(this._task, this._notifier, newState, t));
        }
    }

    private static final class StatefulTask
        implements IStateful, ITask {

        private static final int STATE_RUNNING  = 1;

        private int _state;

        private IStateWatcher _watcher;
        private ITask _task;

        private StatefulTask() {
            this._state = STATE_INIT;
        }

        @Override
        public void setWatcher(IStateWatcher watcher) {
            this._task = ((TaskStateWatcher) watcher)._task;
            this._watcher = watcher;
        }

        @Override
        public IStateWatcher getWatcher() {
            return this._watcher;
        }

        @Override
        public void run() {
            if (this._watcher != null) {
                changeState(STATE_RUNNING);
            }

            try {
                this._task.run();
            } catch (Exception ex) {
                if (this._watcher != null) {
                    int oldState = this._state;
                    this._state = STATE_RUNNING;
                    this._watcher.stateChange(this, oldState, this._state, ex);
                }
            }

            if (this._watcher != null) {
                changeState(STATE_TERMINAL);
            }
        }

        @Override
        public int getPriority() {
            return this._task.getPriority();
        }

        @Override
        public String getDescription() {
            return this._task.getDescription();
        }

        private void changeState(int newState) {
            int oldState = this._state;
            this._state = newState;
            this._watcher.stateChanged(this, oldState, newState);
        }
    }
}
