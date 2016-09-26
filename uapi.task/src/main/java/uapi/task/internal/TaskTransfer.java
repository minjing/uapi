/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.task.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import uapi.KernelException;
import uapi.log.ILogger;
import uapi.task.ITask;

class TaskTransfer
    implements ITaskTransfer {

    private static final int TASK_QUEUE_CAPACITY    = 32;
    private static final int WAIT_TIME              = 1000;

    private ILogger _logger;

    private final List<TaskEmitter> _taskEmitters;
    private final List<TaskRunner> _taskRunners;

    private final TransferTaskJob _transferJob;
    private Thread _transferThread;

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

    void start() {
        this._transferThread = new Thread(this._transferJob);
        this._transferThread.start();
    }

    void stop() {
        if (this._transferThread == null) {
            throw new KernelException("No thread can be stopped.");
        }
        this._transferThread.interrupt();
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
                if (Thread.currentThread().isInterrupted()) {
                    TaskTransfer.this._logger.warn("Receive interrupted signal, the thread will exit.");
                    return;
                }
                for (; idxEmitter < TaskTransfer.this._taskEmitters.size(); idxEmitter++) {
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
                    try {
                        Thread.sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        TaskTransfer.this._logger.error(e, "Encounter InterruptedException when wait receive task, thread will exit.");
                        return;
                    }
                    continue;
                }
                ITask task = null;
                for (; idxRunner < TaskTransfer.this._taskRunners.size(); idxRunner++) {
                    TaskRunner taskRunner = TaskTransfer.this._taskRunners.get(idxRunner);
                    IWritableBuffer<ITask> wbuffer = taskRunner.getBuffer();
                    if (task == null) {
                        try {
                            task = this._taskCache.take();
                        } catch (InterruptedException e) {
                            TaskTransfer.this._logger.error(e, "Encounter InterruptedException when take the task, thread will exit.");
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
