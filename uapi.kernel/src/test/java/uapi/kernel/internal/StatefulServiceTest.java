package uapi.kernel.internal;

import org.junit.Test;

import uapi.kernel.IService;
import uapi.kernel.Init;

public class StatefulServiceTest {

    @Test
    public void testServiceWithoutId() {
        TestService service = new TestService();
    }

    @Test
    public void testServiceWithId() {
        
    }

    @Test
    public void testServiceWithDependency() {
        
    }

    @Test
    public void testServiceInit() {
        
    }

    final class TestService implements IService {

        @Init
        public void init() { }
    }
}
