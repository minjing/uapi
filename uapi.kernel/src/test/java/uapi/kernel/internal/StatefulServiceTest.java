package uapi.kernel.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uapi.kernel.Attribute;
import uapi.kernel.IService;
import uapi.kernel.Init;
import uapi.kernel.Inject;

public class StatefulServiceTest {

    @Mock ServiceRepository _svcRepo;

    @Before
    public void initMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testServiceWithoutId() {
        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getId());
        assertTrue(stateService.hasInitMethod());
        assertFalse(stateService.initAtLaunching());
    }

    @Test
    public void testServiceWithId() {
        TestServiceWithId svcInst = new TestServiceWithId();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals("TestServiceWithId", stateService.getId());
    }

    @Test
    public void testServiceWithDependency() {
        when(this._svcRepo.getService(String.class.getName())).thenReturn("Hello");

        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getId());
        assertEquals("Hello", ((TestService) stateService.getInstance())._message);

        verify(this._svcRepo, times(1)).getService(String.class.getName());
    }

    @Test
    public void testServiceInit() {
        when(this._svcRepo.getService(String.class.getName())).thenReturn("Hello");

        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getId());
        assertTrue(((TestService) stateService.getInstance()).isInit());
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

    @Attribute(sid="TestServiceWithId")
    final class TestServiceWithId implements IService { }
}
