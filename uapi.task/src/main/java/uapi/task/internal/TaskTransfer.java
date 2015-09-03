package uapi.task.internal;

import java.util.ArrayList;
import java.util.List;

import uapi.service.IService;
import uapi.task.ITask;
import uapi.task.ITaskEmitter;

class TaskTransfer
    implements ITaskTransfer, IService{

    private final List<ITaskEmitter> _taskEmitters;

    TaskTransfer() {
        this._taskEmitters = new ArrayList<>();
    }

    public ITaskEmitter createEmitter(TaskManager.TaskConverter taskConverter) {
        return new TaskEmitter(taskConverter);
    }

    @Override
    public void transferTask(ITask task) {
        // TODO: transferTask
    }
}
