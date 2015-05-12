package uapi.kernel;

import java.util.ServiceLoader;

import uapi.kernel.internal.ServiceRepository;

public final class Main {

    public static void main(String[] args) {
        ServiceLoader<IService> svrLoaders = ServiceLoader.load(IService.class);
        ServiceRepository svrRepo = new ServiceRepository();
        for (IService svr : svrLoaders) {
            svrRepo.addService(svr);
        }
//        svrRepo.outputServices();

        System.exit(0);
    }
}
