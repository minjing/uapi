/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.task.internal;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A buffer that support multiple read write threads
 * 
 * @author min
 *
 * @param <T> The item type which can be put in the buffer
 */
public class CircleBuffer<T> implements IReadableBuffer<T>, IWritableBuffer<T> {

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
                this._items[idx]._waitRead.release();
            } else if (isWait) {
                this._items[idx]._waitWrite.release();
            } else {
                break;
            }
        } while (! isWrite);
        return isWrite;
    }

    @Override
    public T read() {
        try {
            return read(false);
        } catch (InterruptedException e) {
            throw new KernelException("Impossible exception was thrown");
        }
    }

    @Override
    public T read(boolean isWait) throws InterruptedException {
        T item = null;
        do {
            int idx = getReadIndex();
            for (int i = 0; i < RETRY_LIMITATION; i++) {
                item = tryRead(idx);
                if (item != null) {
                    break;
                }
            }
            if (item != null) {
                this._items[idx]._waitWrite.release();
            } else if (isWait) {
                this._items[idx]._waitRead.acquire();
            } else {
                break;
            }
        } while (item == null);
        return item;
    }

    private int getWriteIndex() {
        int idx = this._idxWrite.getAndIncrement() % this._capacity;
        if (idx <= this._idxRead.get()) {
            // Todo:
        }
        return idx;
    }

    private int getReadIndex() {
        return this._idxRead.getAndIncrement() % this._capacity;
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
