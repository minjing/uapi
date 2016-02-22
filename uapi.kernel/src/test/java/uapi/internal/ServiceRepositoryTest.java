package uapi.internal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uapi.service.IService1;
import uapi.service.Inject;
import uapi.service.Registration;
import uapi.service.Type;
import uapi.test.MockitoTest;

public class ServiceRepositoryTest extends MockitoTest {

    private Service1Repository _svcRepo;

    @Before
    public void before() {
        super.before();
        this._svcRepo = new Service1Repository();
    }

    @Test
    public void testAddNoIdService() {
        Object svcInst = new NoIdService1();
        this._svcRepo.addService(svcInst);
        assertEquals(svcInst, this._svcRepo.getService(NoIdService1.class, null));
        assertEquals(svcInst, this._svcRepo.getService(NoIdService1.class.getName(), null));
    }

    @Test
    public void testAddTypeService() {
        Object svcInst = new TypeService1();
        this._svcRepo.addService(svcInst);
        assertEquals(svcInst, this._svcRepo.getService(IService1.class, null));
        assertEquals(svcInst, this._svcRepo.getService(IService1.class.getName(), null));
    }

    @Test
    public void testAddIdService() {
        Object svcInst = new IdService1();
        this._svcRepo.addService(svcInst);
        assertEquals(svcInst, this._svcRepo.getService("NamedService", null));
    }

    @Test
    public void testOutterService() {
        Object svcInst = new OutterService();
        this._svcRepo.addService(svcInst, "OutterService");
        assertEquals(svcInst, this._svcRepo.getService("OutterService", null));
    }

    @Test
    public void testAddMultipleService() {
        this._svcRepo.addService(new Service11());
        this._svcRepo.addService(new Service12());
        assertEquals(2, this._svcRepo.getServices("IService1", null).length);
    }

    @Test
    public void testDependentServices() {
        Service4 svc4 = new Service4();
        this._svcRepo.addService(new Service3());
        this._svcRepo.addService(svc4);
        Service3 svc3 = this._svcRepo.getService(Service3.class, null);
        assertEquals(svc4, svc3._service);
    }

    private class NoIdService1 implements IService1 { }

    @Registration(names="NamedService")
    private class IdService1 implements IService1 { }

    @Registration({ @Type(IService1.class) })
    private class TypeService1 implements IService1 { }

    private class OutterService { }

    @Registration(names="IService1")
    private class Service11 implements IService1 { }

    @Registration(names="IService1")
    private class Service12 implements IService1 { }

    private class Service3 {

        @Inject
        private Service4 _service;

        @SuppressWarnings("unused")
        public void setService(Service4 svc) {
            this._service = svc;
        }
    }

    private class Service4 { }
}
