package uapi.task.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import uapi.log.ILogger;
import uapi.service.IService;
import uapi.service.Inject;
import uapi.task.ITask;

class TaskTransfer
    implements ITaskTransfer, IService{

    private static final int TASK_QUEUE_CAPACITY    = 32;

    @Inject
    private ILogger _logger;

    private final List<TaskEmitter> _taskEmitters;
    private final List<TaskRunner> _taskRunners;

    private final TransferTaskJob _transferJob;

    TaskTransfer() {
        this._taskEmitters = new CopyOnWriteArrayList<>();
        this._taskRunners = new CopyOnWriteArrayList<>();
        this._transferJob = new TransferTaskJob();
    }

    public void setLogger(ILogger logger) {
        this._logger = logger;
    }

    void addTaskEmitter(TaskEmitter taskEmitter) {
        this._taskEmitters.add(taskEmitter);
    }

    void addTaskRunner(TaskRunner taskRunner) {
        this._taskRunners.add(taskRunner);
    }

    @Override
    public void transferTask(ITask task) {
        this._transferJob._taskCache.put(task);
    }
    
    private final class TransferTaskJob
        implements Runnable {

        private final PriorityBlockingQueue<ITask> _taskCache;
        
        private TransferTaskJob() {
            this._taskCache = new PriorityBlockingQueue<>(TASK_QUEUE_CAPACITY, new TaskOrder());
        }

        @Override
        public void run() {
            int idxEmitter = 0;
            int idxRunner = 0;

            while (true) {
                for (; idxEmitter < TaskTransfer.this._taskEmitters.size() ; idxEmitter++) {
                    TaskEmitter taskEmitter = TaskTransfer.this._taskEmitters.get(idxEmitter);
                    IReadableBuffer<ITask> buffer = taskEmitter.getBuffer();
                    ITask task = buffer.read();
                    if (task == null) {
                        continue;
                    }
                    this._taskCache.put(task);
                    if (this._taskCache.size() >= TASK_QUEUE_CAPACITY) {
                        break;
                    }
                }
                idxEmitter = idxEmitter % TaskTransfer.this._taskEmitters.size();
                if (this._taskCache.isEmpty()) {
                    // TODO: Maybe we can wait for while
                    continue;
                }
                ITask task = null;
                for (; idxRunner < TaskTransfer.this._taskRunners.size(); idxRunner++) {
                    TaskRunner taskRunner = TaskTransfer.this._taskRunners.get(idxRunner);
                    IWriteableBuffer<ITask> wbuffer = taskRunner.getBuffer();
                    if (task == null) {
                        try {
                            task = this._taskCache.take();
                        } catch (InterruptedException e) {
                            TaskTransfer.this._logger.error(e, "Encounter an exception when take the task");
                            return;
                        }
                    }
                    boolean isWrite = wbuffer.write(task);
                    if (isWrite) {
                        task = null;
                    }
                }
            }
        }
    }
}
