package uapi.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import uapi.TestBase;
import uapi.internal.ServiceRepository;
import uapi.internal.StatefulService;
import uapi.service.IService;
import uapi.service.Inject;
import uapi.service.OnInit;
import uapi.service.Registration;
import uapi.service.Type;

@Ignore
public class StatefulServiceTest extends TestBase {

    @Mock ServiceRepository _svcRepo;

    @Test
    public void testServiceWithoutId() {
        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getName());
        assertTrue(stateService.hasInitMethod());
        assertTrue(stateService.isLazyInit());
    }

    @Test
    public void testServiceWithId() {
        TestServiceWithId svcInst = new TestServiceWithId();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals("TestServiceWithId", stateService.getName());
    }

    @Test
    public void testServiceWithDependency() {
        when(this._svcRepo.getService(String.class.getName(), null)).thenReturn("Hello");

        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getName());
        assertEquals("Hello", ((TestService) stateService.getInstance(null))._message);

        verify(this._svcRepo, times(1)).getService(String.class.getName(), null);
    }

    @Test
    public void testServiceInit() {
        when(this._svcRepo.getService(String.class.getName(), null)).thenReturn("Hello");

        TestService svcInst = new TestService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService.class.getName(), stateService.getName());
        assertTrue(((TestService) stateService.getInstance(null)).isInit());
    }

    @Test
    public void testServiceWithType() {
        TestServiceWithType svcInst = new TestServiceWithType();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(IService.class.getName(), stateService.getName());
    }

    @Test
    public void testMultiple() {
        when(this._svcRepo.getServices(String.class.getName(), null)).thenReturn(new String[] { "Min", "Jing" });

        MultipleDependenciesService svcInst = new MultipleDependenciesService();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        MultipleDependenciesService inst = stateService.getInstance(null);
        assertEquals(svcInst, inst);
        assertTrue(inst._messages.contains("Min"));
        assertTrue(inst._messages.contains("Jing"));
    }

    final class TestService implements IService {

        private boolean _initialized = false;

        @Inject
        private String _message;

        public void setMessage(String msg) {
            this._message = msg;
        }

        @OnInit
        public void init() {
            this._initialized = true;
        }

        boolean isInit() {
            return this._initialized;
        }
    }

    @Registration(names="TestServiceWithId")
    final class TestServiceWithId implements IService { }

    @Registration({@Type(IService.class)})
    final class TestServiceWithType implements IService { }

    final class MultipleDependenciesService {

        @Inject
        private List<String> _messages = new ArrayList<>();

        public void setMessage(String msg) {
            this._messages.add(msg);
        }
    }
}
