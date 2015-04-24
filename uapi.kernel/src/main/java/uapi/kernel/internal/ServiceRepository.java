package uapi.kernel.internal;

import java.util.List;
import java.util.ArrayList;

import uapi.kernel.IService;

public final class ServiceRepository {

    private final List<IService> services;

    public ServiceRepository() {
        this.services = new ArrayList<>();
    }

    public void addService(IService service) {
        this.services.add(service);
    }

    public void outputServices() {
        for (IService svr : this.services) {
            System.out.println(svr);
        }
    }
}
