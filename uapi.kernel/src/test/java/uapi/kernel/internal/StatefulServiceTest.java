package uapi.kernel.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;

import uapi.kernel.Attribute;
import uapi.kernel.IService;
import uapi.kernel.Init;
import uapi.kernel.Inject;
import uapi.kernel.TestBase;

public class StatefulServiceTest extends TestBase {

    @Mock ServiceRepository _svcRepo;

    @Test
    public void testServiceWithoutId() {
        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getName());
        assertTrue(stateService.hasInitMethod());
        assertFalse(stateService.initAtLaunching());
    }

    @Test
    public void testServiceWithId() {
        TestServiceWithId svcInst = new TestServiceWithId();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals("TestServiceWithId", stateService.getName());
    }

    @Test
    public void testServiceWithDependency() {
        when(this._svcRepo.getService(String.class.getName())).thenReturn("Hello");

        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getName());
        assertEquals("Hello", ((TestService) stateService.getInstance())._message);

        verify(this._svcRepo, times(1)).getService(String.class.getName());
    }

    @Test
    public void testServiceInit() {
        when(this._svcRepo.getService(String.class.getName())).thenReturn("Hello");

        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getName());
        assertTrue(((TestService) stateService.getInstance()).isInit());
    }

    @Test
    public void testServiceWithType() {
        TestServiceWithType svcInst = new TestServiceWithType();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(IService.class.getName(), stateService.getName());
    }

    @Test
    public void testMultiple() {
        when(this._svcRepo.getService(String.class.getName())).thenReturn("Min");

        MultipleDependenciesService svcInst = new MultipleDependenciesService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
    }

    final class TestService implements IService {

        private boolean _initialized = false;

        @Inject
        private String _message;

        public void setMessage(String msg) {
            this._message = msg;
        }

        @Init
        public void init() {
            this._initialized = true;
        }

        boolean isInit() {
            return this._initialized;
        }
    }

    @Attribute(name="TestServiceWithId")
    final class TestServiceWithId implements IService { }

    @Attribute(type=IService.class)
    final class TestServiceWithType implements IService { }

    final class MultipleDependenciesService {

        @Inject
        private List<String> _messages = new ArrayList<>();

        public void setMessage(String msg) {
            this._messages.add(msg);
        }
    }
}
