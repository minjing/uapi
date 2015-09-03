package uapi.task;

/**
 * A buffer which can be only read item
 * 
 * @author min
 */
public interface IReadableBuffer<T> {

    T read();

    T read(boolean isWait) throws InterruptedException;
}
