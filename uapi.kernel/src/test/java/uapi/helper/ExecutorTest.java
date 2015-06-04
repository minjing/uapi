package uapi.helper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class ExecutorTest {

    @Test
    public void testGuardRun() {
        FakeLock lock = new FakeLock();
        final List<String> strs = new ArrayList<>();
        Executor.create().guardBy(lock).run(() -> { strs.add("Hello"); });
        assertEquals(1, strs.size());
        assertEquals("Hello", strs.get(0));
        assertEquals(1, lock._lockCount);
        assertEquals(1, lock._unlockCount);
    }

    @Test
    public void testNoGuardRun() {
        FakeLock lock = new FakeLock();
        final List<String> strs = new ArrayList<>();
        Executor.create().run(() -> { strs.add("Hello"); });
        assertEquals(1, strs.size());
        assertEquals("Hello", strs.get(0));
        assertEquals(0, lock._lockCount);
        assertEquals(0, lock._unlockCount);
    }

    @Test
    public void testGuardResult() {
        FakeLock lock = new FakeLock();
        String result = Executor.create().guardBy(lock).getResult(() -> { return "Hello"; });
        assertEquals("Hello", result);
        assertEquals(1, lock._lockCount);
        assertEquals(1, lock._unlockCount);
    }

    @Test
    public void testNoGuardResult() {
        FakeLock lock = new FakeLock();
        String result = Executor.create().getResult(() -> { return "Hello"; });
        assertEquals("Hello", result);
        assertEquals(0, lock._lockCount);
        assertEquals(0, lock._unlockCount);
    }

    private class FakeLock extends ReentrantLock {

        private static final long serialVersionUID = 1L;

        private int _lockCount = 0;
        private int _unlockCount = 0;

        @Override
        public void lock() {
            this._lockCount++;
        }

        @Override
        public void unlock() {
            this._unlockCount++;
        }
    }
}
