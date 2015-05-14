package uapi.kernel.internal;

import static org.junit.Assert.*;

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
        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getId());
    }

    @Test
    public void testServiceInit() {
        
    }

    final class TestService implements IService {

        @Inject
        private String _message;

        @Init
        public void init() { }
    }

    @Attribute(sid="TestServiceWithId")
    final class TestServiceWithId implements IService { }
}
