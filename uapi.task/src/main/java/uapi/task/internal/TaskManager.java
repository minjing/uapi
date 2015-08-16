package uapi.task.internal;

import uapi.IStateWatcher;
import uapi.IStateful;
import uapi.config.Config;
import uapi.helper.ArgumentChecker;
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
            if (newState == IStateful.STATE_TERMINAL) {
                this._notifier.onDone(this._task);
            }
        }

        @Override
        public void stateChange(IStateful which, int oldState, int newState, Throwable t) {
            ArgumentChecker.isEmpty(which, "which");
            if (t != null) {
                this._notifier.onFailed(this._task, t);
            } else if (newState == IStateful.STATE_TERMINAL) {
                this._notifier.onDone(this._task);
            }
        }
    }
}
