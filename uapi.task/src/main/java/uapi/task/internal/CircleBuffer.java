package uapi.task.internal;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;

/**
 * A buffer that support multiple read write threads
 * 
 * @author min
 *
 * @param <T> The item type which can be put in the buffer
 */
public class CircleBuffer<T> implements IReadableBuffer<T>, IWriteableBuffer<T> {

    private static final int RETRY_LIMITATION   = 16;

    private final int _capacity;
    private final Item<T>[] _items;

    private AtomicInteger _idxRead;
    private AtomicInteger _idxWrite;

    @SuppressWarnings("unchecked")
    public CircleBuffer(int capacity) {
        ArgumentChecker.checkInt(capacity, "capacity", 1, Integer.MAX_VALUE);
        this._idxWrite = new AtomicInteger(0);
        this._idxRead = new AtomicInteger(0);
        this._capacity = capacity;
        this._items = new Item[capacity];
        for (int i = 0; i < capacity; i++) {
            this._items[i] = new Item<>();
        }
    }

    @Override
    public boolean write(T item) {
        try {
            return write(item, false);
        } catch (InterruptedException e) {
            throw new KernelException("Unpossiable exception was thrown");
        }
    }

    @Override
    public boolean write(T item, boolean isWait) throws InterruptedException {
        boolean isWrite = false;
        int idx = getWriteIndex();
        do {
            for (int i = 0; i < RETRY_LIMITATION; i++) {
                isWrite = tryWrite(idx, item);
                if (isWrite) {
                    break;
                }
            }
            if (isWrite) {
                this._items[idx]._waitWrite.release();
                break;
            }
            if (! isWait) {
                break;
            }
            this._items[idx]._waitRead.acquire();
        } while (! isWrite);
        return isWrite;
    }

    @Override
    public T read() {
        try {
            return read(false);
        } catch (InterruptedException e) {
            throw new KernelException("Unpossiable exception was thrown");
        }
    }

    @Override
    public T read(boolean isWait) throws InterruptedException {
        T item = null;
        int idx = getReadIndex();
        do {
            for (int i = 0; i < RETRY_LIMITATION; i++) {
                item = tryRead(idx);
                if (item != null) {
                    break;
                }
            }
            if (item != null) {
                this._items[idx]._waitWrite.release();
                break;
            }
            if (! isWait) {
                break;
            }
            this._items[idx]._waitRead.acquire();
        } while (item == null);
        return item;
    }

    private int getWriteIndex() {
        int idx = this._idxWrite.getAndIncrement() % this._capacity;
        return idx;
    }

    private int getReadIndex() {
        int idx = this._idxRead.getAndIncrement() % this._capacity;
        return idx;
    }

    private boolean tryWrite(int idx, T item) throws InterruptedException {
        if (this._items[idx].get() != null) {
            return false;
        }
        return this._items[idx].compareAndSet(null, item);
    }

    private T tryRead(int idx) throws InterruptedException {
        if (this._items[idx] == null) {
            return null;
        }
        return this._items[idx].getAndSet(null);
    }

    private static final class Item<T> {

        private AtomicReference<T> _item;
        private final Semaphore _waitRead;
        private final Semaphore _waitWrite;

        private Item() {
            this._waitRead = new Semaphore(0);
            this._waitWrite = new Semaphore(0);
            this._item = new AtomicReference<>();
        }

        private T getAndSet(T newValue) {
            return this._item.getAndSet(newValue);
        }

        private boolean compareAndSet(T expect, T update) {
            return this._item.compareAndSet(expect, update);
        }

        private T get() {
            return this._item.get();
        }
    }
}
