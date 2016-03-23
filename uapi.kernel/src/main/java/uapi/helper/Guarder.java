package uapi.helper;

import java.util.concurrent.locks.Lock;

/**
 * Created by xquan on 3/23/2016.
 */
public class Guarder {

    public static Guarder by(final Lock lock) {
        ArgumentChecker.notNull(lock, "lock");
        return new Guarder(lock);
    }

    private final Lock _lock;

    private Guarder(final Lock lock) {
        this._lock = lock;
    }

    public void run(Runnable run) {
        this._lock.lock();
        try {
            run.run();
        } finally {
            this._lock.unlock();
        }
    }

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
