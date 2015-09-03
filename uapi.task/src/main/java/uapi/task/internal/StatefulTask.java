package uapi.task.internal;

import uapi.IStateWatcher;
import uapi.IStateful;
import uapi.task.ITask;

final class StatefulTask
    implements IStateful, ITask {

    private static final int STATE_RUNNING  = 1;

    private int _state;

    private IStateWatcher _watcher;
    private ITask _task;

    StatefulTask(ITask task) {
        this._task = task;
        this._state = STATE_INIT;
    }

    @Override
    public void setWatcher(IStateWatcher watcher) {
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

    @Override
    public int getState() {
        return this._state;
    }

    private void changeState(int newState) {
        int oldState = this._state;
        this._state = newState;
        this._watcher.stateChanged(this, oldState, newState);
    }
}
