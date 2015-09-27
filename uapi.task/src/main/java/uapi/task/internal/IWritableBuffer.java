package uapi.task.internal;

/**
 * A buffer which only can be write item to it.
 * 
 * @author min
 */
public interface IWritableBuffer<T> {

    /**
     * Write item to buffer, if write failed it will return false;
     * 
     * @param   item
     *          The item which will be added
     * @return  True means write item success otherwise return false
     */
    boolean write(T item);

    /**
     * Write item to buffer, if it success it return true otherwise return false
     * If the isWait is set to true, the the thread will be blocked if the buffer
     * is full or the buffer is at read model.
     * 
     * @param   item
     *          The item which will be added to the buffer
     * @param   isWait
     *          If it is set to true, the method will be blocked until the buffer
     *          become un-full or the buffer is not at read model
     * @return  True means write item success otherwise return false
     * @throws  InterruptedException
     *          When it is waiting for un-full buffer or waiting for the buffer
     *          can be read
     */
    boolean write(T item, boolean isWait) throws InterruptedException;
}
