/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.task.internal;

import uapi.IStateWatcher;
import uapi.IStateful;
import uapi.log.ILogger;
import uapi.task.ITask;

public class TaskRunner implements Runnable {

    private final ILogger _logger;
    private final Buffer<ITask> _buffer;

    TaskRunner(ILogger logger) {
        this._logger = logger;
        this._buffer = new Buffer<>();
    }

    IWritableBuffer<ITask> getBuffer() {
        return this._buffer;
    }

    @Override
    public void run() {
        while (true) {
            ITask task = this._buffer.read();
            try {
                task.run();
            } catch (Exception ex) {
                notify(task, ex);
            }
        }
    }

    private void notify(ITask task, Throwable t) {
        if (task instanceof IStateful) {
            IStateful statefulTask = (IStateful) task;
            IStateWatcher watcher = statefulTask.getWatcher();
            if (t != null) {
                watcher.stateChange(statefulTask, statefulTask.getState(), IStateful.STATE_TERMINAL, t);
            } else {
                watcher.stateChanged(statefulTask, statefulTask.getState(), IStateful.STATE_TERMINAL);
            }
        } else {
            if (t != null) {
                this._logger.error(t, "Execute task failed - {}", task.getDescription());
            }
        }
    }
}
