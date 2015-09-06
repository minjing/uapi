package uapi.task.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        this._capacity = capacity;
        this._items = new Item[capacity];
        this._idxWrite = new AtomicInteger(0);
        this._idxRead = new AtomicInteger(0);
    }

    @Override
    public boolean write(T item) {
        return write(item);
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
            if (! isWait) {
                break;
            }
            // TODO: wait / notify
        } while (! isWrite);
        return isWrite;
    }

    private int getWriteIndex() {
        int idx = this._idxWrite.getAndIncrement() % this._capacity;
        return idx;
    }

    private boolean tryWrite(int idx, T item) throws InterruptedException {
        if (this._items[idx] != null) {
            return false;
        }
        this._items[idx] = new Item<T>(item);
        return true;
    }

    @Override
    public T read() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T read(boolean isWait) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    private final class Item<T> {

        private final T _item;
        private AtomicReference<Action> _action;

        private Item(T item) {
            this._item = item;
            this._action = new AtomicReference<>(Action.NONE);
        }
    }

    private enum Action {
        READING, WRITING, NONE
    }
}
