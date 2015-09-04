package uapi.task.internal;

import uapi.helper.ArgumentChecker;
import uapi.task.INotifier;
import uapi.task.ITask;
import uapi.task.ITaskEmitter;

public class TaskEmitter implements ITaskEmitter {

    private final TaskManager.TaskConverter _taskConverter;
    private final Buffer<ITask> _taskBuffer;

    TaskEmitter(TaskManager.TaskConverter taskConverter) {
        this._taskConverter = taskConverter;
        this._taskBuffer = new Buffer<>();
    }

    public IReadableBuffer<ITask> getBuffer() {
        return this._taskBuffer;
    }

    @Override
    public void emit(ITask task) {
        emit(task, null);
    }

    @Override
    public void emit(ITask task, INotifier notifier) {
        ArgumentChecker.isEmpty(task, "task");
        ITask newTask = this._taskConverter.convert(task, notifier);
        this._taskBuffer.write(newTask);
    }
}
