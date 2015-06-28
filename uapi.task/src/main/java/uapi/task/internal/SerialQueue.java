package uapi.task.internal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.KernelException;
import uapi.task.ITask;

final class SerialQueue {

    private final Lock _lock;
    private int _capacity;
    private final Queue<ITask> _tasks;

    SerialQueue(final int capacity) {
        if (capacity <= 0) {
            throw new InvalidArgumentException("The capacity must be more then 0 - ", capacity);
        }
        this._capacity = capacity;
        this._tasks = new LinkedList<>();
        this._lock = new ReentrantLock();
    }

    void put(final ITask task) {
        if (task == null) {
            throw new InvalidArgumentException("task", InvalidArgumentType.EMPTY);
        }
        this._lock.lock();
        try {
            int taskCount = this._tasks.size();
            if (taskCount >= this._capacity) {
                throw new KernelException("The queue size {} has over the limitation {}", taskCount, this._capacity);
            }
            this._tasks.add(task);
        } finally {
            this._lock.unlock();
        }
    }

    ITask get() {
        this._lock.lock();
        try {
            int taskCount = this._tasks.size();
            if (taskCount > 0) {
                return this._tasks.poll();
            }
            return null;
        } finally {
            this._lock.unlock();
        }
    }

    void setSize(int capacity) {
        if (capacity <= 0) {
            throw new InvalidArgumentException("The capacity must be more then 0 - {}", capacity);
        }
        this._capacity = capacity;
    }
}
