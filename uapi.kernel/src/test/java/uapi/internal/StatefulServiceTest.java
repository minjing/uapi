package uapi.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;

import uapi.service.IService1;
import uapi.service.Inject;
import uapi.service.OnInit;
import uapi.service.Registration;
import uapi.service.Type;
import uapi.test.MockitoTest;

public class StatefulServiceTest extends MockitoTest {

    @Mock
    Service1Repository _svcRepo;

    @Test
    public void testServiceWithoutId() {
        TestService1 svcInst = new TestService1();
        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService1.class.getName(), stateService.getName());
        assertTrue(stateService.hasInitMethod());
        assertTrue(stateService.isLazyInit());
    }

    @Test
    public void testServiceWithDependency() {
        TestService1 svcInst = new TestService1();
        when(this._svcRepo.getService(String.class.getName(), svcInst)).thenReturn("Hello");

        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService1.class.getName(), stateService.getName());
        assertEquals("Hello", ((TestService1) stateService.getInstance(null))._message);

        verify(this._svcRepo, times(1)).getService(String.class.getName(), svcInst);
    }

    @Test
    public void testServiceInit() {
        TestService1 svcInst = new TestService1();

        when(this._svcRepo.getService(String.class.getName(), svcInst)).thenReturn("Hello");

        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        assertEquals(TestService1.class.getName(), stateService.getName());
        assertTrue(((TestService1) stateService.getInstance(null)).isInit());
    }

    @Test
    public void testMultiple() {
        MultipleDependenciesService svcInst = new MultipleDependenciesService();

        when(this._svcRepo.getServices(String.class.getName(), svcInst)).thenReturn(new String[] { "Min", "Jing" });

        StatefulService stateService = new StatefulService(this._svcRepo, svcInst);
        MultipleDependenciesService inst = stateService.getInstance(null);
        assertEquals(svcInst, inst);
        assertTrue(inst._messages.contains("Min"));
        assertTrue(inst._messages.contains("Jing"));
    }

    final class TestService1 implements IService1 {

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

    @Registration(names="TestService1WithId")
    final class TestService1WithId implements IService1 { }

    @Registration({@Type(IService1.class)})
    final class TestService1WithType implements IService1 { }

    final class MultipleDependenciesService {

        @Inject
        private List<String> _messages = new ArrayList<>();

        public void addMessage(String msg) {
            this._messages.add(msg);
        }
    }
}
