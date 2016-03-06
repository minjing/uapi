package uapi.helper;

import java.util.concurrent.locks.Lock;

import com.sun.org.apache.xpath.internal.Arg;
import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

public final class Executor {

    private Lock _lock;

    private Executor() { }

    public static Executor create() {
        return new Executor();
    }

    public Executor guardBy(Lock lock) {
        ArgumentChecker.notNull(lock, "lock");
        this._lock = lock;
        return this;
    }

    public void run(Runnable run) {
        ArgumentChecker.notNull(run, "run");
        if (this._lock != null) {
            runByGuard(run);
        } else {
            run.run();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T runForResult(Resultful<?> run) {
        ArgumentChecker.notNull(run, "run");
        Object rtn;
        if (this._lock != null) {
            rtn = resultByGuard(run);
        } else {
            rtn = run.run();
        }
        return (T) rtn;
    }

    private void runByGuard(Runnable run) {
        this._lock.lock();
        try {
            run.run();
        } finally {
            this._lock.unlock();
        }
    }

    private Object resultByGuard(Resultful<?> run) {
        this._lock.lock();
        try {
            return run.run();
        } finally {
            this._lock.unlock();
        }
    }
}
