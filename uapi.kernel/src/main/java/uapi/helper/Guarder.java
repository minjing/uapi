package uapi.helper;

import java.util.concurrent.locks.Lock;

/**
 * A Guarder protected one or more statement by specific lock to make these statements
 * are thread safe.
 */
public class Guarder {

    /**
     * Create a new Guarder by specific lock
     *
     * @param   lock
     *          The lock used to protected one or more statement
     * @return  The Guarder instance
     */
    public static Guarder by(final Lock lock) {
        ArgumentChecker.notNull(lock, "lock");
        return new Guarder(lock);
    }

    private final Lock _lock;

    private Guarder(final Lock lock) {
        this._lock = lock;
    }

    /**
     * Run specific runnable statements
     *
     * @param   run
     *          Some runnable statements
     */
    public void run(Runnable run) {
        this._lock.lock();
        try {
            run.run();
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * Run specific runnable statements and retrieve its result
     * @param   run
     *          Some runnable statements
     * @param   <T>
     *          The return type
     * @return
     */
    public <T> T runForResult(Resultful<T> run) {
        T rtn = null;
        this._lock.lock();
        try {
            rtn = run.run();
        } finally {
            this._lock.unlock();
        }
        return rtn;
    }
}
