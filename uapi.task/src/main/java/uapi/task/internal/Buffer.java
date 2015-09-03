package uapi.task.internal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import uapi.KernelException;
import uapi.task.IReadableBuffer;
import uapi.task.IWriteableBuffer;

public class Buffer<T> implements IReadableBuffer<T>, IWriteableBuffer<T> {

    private static final int TAG_NONE   = 0;
    private static final int TAG_READ   = 1;
    private static final int TAG_WRITE  = 2;

    private static final int RETRY_LIMIT    = 16;
    private static final int CAPACITY       = 128;

    private final Queue<T> _buffer;
    private final AtomicInteger _rwTag;
    private final Object _locker;

    public Buffer() {
        this._rwTag = new AtomicInteger(TAG_NONE);
        this._buffer = new LinkedList<>();
        this._locker = new Object();
    }

    public void write(T item) {
        try {
            write(item, false);
        } catch (InterruptedException ex) {
            throw new KernelException(ex, "An impossible exception was thrown");
        }
    }

    @Override
    public void write(T item, boolean isWait) throws InterruptedException {
        boolean isWrite = false;
        do {
            isWrite = writeItem(item);
            if (! isWait) {
                break;
            }
        } while (isWrite);
    }

    private boolean writeItem(T item) {
        boolean canWrite = false;
        int tryCount = 0;
        for (; tryCount < RETRY_LIMIT; tryCount++) {
            canWrite = this._rwTag.compareAndSet(TAG_NONE, TAG_WRITE);
            if (canWrite) {
                break;
            }
        }
        if (canWrite) {
            for (; tryCount < RETRY_LIMIT; tryCount++) {
                if (this._buffer.size() >= CAPACITY) {
                    canWrite = false;
                    this._rwTag.compareAndSet(TAG_WRITE, TAG_NONE);
                } else if (this._rwTag.get() == TAG_WRITE) {
                    canWrite = true;
                    break;
                } else {
                    canWrite = this._rwTag.compareAndSet(TAG_NONE, TAG_WRITE);
                    if (canWrite) {
                        break;
                    }
                }
            }
            if (canWrite) {
                this._buffer.add(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public T read() {
        try {
            return read(false);
        } catch (InterruptedException ex) {
            throw new KernelException(ex, "An impossible exception was thrown");
        }
    }

    @Override
    public T read(boolean isWait) throws InterruptedException {
        T item = null;
        do {
            item = readItem();
            if (! isWait) {
                break;
            }
        } while (item != null);
        return item;
    }

    private T readItem() {
        boolean canRead = false;
        int tryCount = 0;
        for (; tryCount < RETRY_LIMIT; tryCount++) {
            canRead = this._rwTag.compareAndSet(TAG_NONE, TAG_READ);
            if (canRead) {
                break;
            }
        }
        if (canRead) {
            for (; tryCount < RETRY_LIMIT; tryCount++) {
                if (this._buffer.size() <= 0) {
                    canRead = false;
                    this._rwTag.compareAndSet(TAG_READ, TAG_NONE);
                } else if (this._rwTag.get() == TAG_READ) {
                    canRead = true;
                    break;
                } else {
                    canRead = this._rwTag.compareAndSet(TAG_NONE, TAG_READ);
                    if (canRead) {
                        break;
                    }
                }
            }
            if (canRead) {
                T t = this._buffer.remove();
                synchronized (this._locker) {
                    this._locker.notifyAll();
                }
                return t;
            }
        }
        return null;
    }
}
