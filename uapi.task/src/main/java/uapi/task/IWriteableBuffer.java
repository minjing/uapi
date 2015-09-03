package uapi.task;

/**
 * A buffer which only can be write item to it.
 * 
 * @author min
 */
public interface IWriteableBuffer<T> {

    /**
     * Write item to buffer
     * 
     * @param item  The item which will be added
     */
    void write(T item);
}
