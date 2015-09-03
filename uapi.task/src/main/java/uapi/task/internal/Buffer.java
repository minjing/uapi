package uapi.task.internal;

import java.util.LinkedList;
import java.util.Queue;

import uapi.task.IReadableBuffer;
import uapi.task.IWriteableBuffer;

public class Buffer<T> implements IReadableBuffer<T>, IWriteableBuffer<T> {

    private final Queue<T> _buffer;

    public Buffer() {
        this._buffer = new LinkedList<>();
    }

    @Override
    public void write(T item) {
        this._buffer.add(item);
    }

    @Override
    public T read() {
        return this._buffer.remove();
    }
}
