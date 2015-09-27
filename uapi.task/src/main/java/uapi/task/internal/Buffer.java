package uapi.task.internal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;

/**
 * A buffer is a fifo queue wrapper, it only support read/write at TWO threads environment.
 * Notice do NOT use the Buffer at multiple read/write threads environment
 * 
 * @author min
 *
 * @param <T>   The item type
 */
public class Buffer<T> implements IReadableBuffer<T>, IWritableBuffer<T> {

    // The tag means the buffer is at free state, it can be convert to read or write model
    private static final int TAG_NONE   = 0;
    // The tag means the buffer is at read model, it can't be write
    private static final int TAG_READ   = 1;
    // The tag means the buffer is at write model, it can't be read
    private static final int TAG_WRITE  = 2;

    private static final int RETRY_LIMIT        = 16;
    private static final int DEFAULT_CAPACITY   = 128;

    private final Queue<T> _buffer;
    private final AtomicInteger _rwTag;
    private final Object _locker;
    private final int _capacity;

    public Buffer() {
        this(DEFAULT_CAPACITY);
    }

    public Buffer(int capacity) {
        ArgumentChecker.checkInt(capacity, "capacity", 1, Integer.MAX_VALUE);
        this._rwTag = new AtomicInteger(TAG_NONE);
        this._buffer = new LinkedList<>();
        this._locker = new Object();
        this._capacity = capacity;
    }

    public boolean write(T item) {
        boolean isWrite = false;
        try {
            isWrite = write(item, false);
        } catch (InterruptedException ex) {
            throw new KernelException(ex, "An impossible exception was thrown");
        }
        return isWrite;
    }

    @Override
    public boolean write(T item, boolean isWait) throws InterruptedException {
        boolean isWrite = false;
        do {
            isWrite = writeItem(item);
            if (isWrite || ! isWait) {
                break;
            }
            synchronized (this._locker) {
                this._locker.wait();
            }
        } while (! isWrite);
        return isWrite;
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
                if (this._buffer.size() >= this._capacity) {
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
                boolean success = this._rwTag.compareAndSet(TAG_WRITE, TAG_NONE);
                synchronized (this._locker) {
                    this._locker.notifyAll();
                }
                if (! success) {
                    throw new KernelException("Swich write model to normal model failed");
                }
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
            if (item != null || ! isWait) {
                break;
            }
            synchronized (this._locker) {
                this._locker.wait();
            }
        } while (item == null);
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
                boolean success = this._rwTag.compareAndSet(TAG_READ, TAG_NONE);
                if (! success) {
                    throw new KernelException("Switch read model to normal model failed");
                }
                synchronized (this._locker) {
                    this._locker.notifyAll();
                }
                return t;
            }
        }
        return null;
    }
}
