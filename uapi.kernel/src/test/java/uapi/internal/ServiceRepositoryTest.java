package uapi.internal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uapi.TestBase;
import uapi.internal.ServiceRepository;
import uapi.service.IService;
import uapi.service.Inject;
import uapi.service.Registration;

public class ServiceRepositoryTest extends TestBase {

    private ServiceRepository _svcRepo;

    @Before
    public void before() {
        super.before();
        this._svcRepo = new ServiceRepository();
    }

    @Test
    public void testAddNoIdService() {
        Object svcInst = new NoIdService();
        this._svcRepo.addService(svcInst);
        assertEquals(svcInst, this._svcRepo.getService(NoIdService.class));
        assertEquals(svcInst, this._svcRepo.getService(NoIdService.class.getName()));
    }

    @Test
    public void testAddIdService() {
        Object svcInst = new IdService();
        this._svcRepo.addService(svcInst);
        assertEquals(svcInst, this._svcRepo.getService("NamedService"));
    }

    @Test
    public void testOutterService() {
        Object svcInst = new OutterService();
        this._svcRepo.addService(svcInst, "OutterService");
        assertEquals(svcInst, this._svcRepo.getService("OutterService"));
    }

    @Test
    public void testAddMultipleService() {
        this._svcRepo.addService(new Service1());
        this._svcRepo.addService(new Service2());
        assertEquals(2, this._svcRepo.getServices("IService").length);
    }

    @Test
    public void testDependentServices() {
        Service4 svc4 = new Service4();
        this._svcRepo.addService(new Service3());
        this._svcRepo.addService(svc4);
        Service3 svc3 = this._svcRepo.getService(Service3.class);
        assertEquals(svc4, svc3._service);
    }

    private class NoIdService implements IService { }

    @Registration(names="NamedService")
    private class IdService implements IService { }

    private class OutterService { }

    @Registration(names="IService")
    private class Service1 implements IService { }

    @Registration(names="IService")
    private class Service2 implements IService { }

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
