package uapi.kernel;

import java.util.ServiceLoader;
import java.util.concurrent.Semaphore;

import uapi.kernel.internal.ServiceRepository;
import uapi.log.ILogger;

public final class Main {

    private static final Semaphore          semaphore;
    private static final ServiceRepository  svcRepo;

    static {
        semaphore   = new Semaphore(1);
        svcRepo     = new ServiceRepository();
    }

    public static void main(String[] args) {
        ServiceLoader<IService> svrLoaders = ServiceLoader.load(IService.class);
        for (IService svr : svrLoaders) {
            svcRepo.addService(svr);
        }

        // Retrieve the log service
        ILogger logger = svcRepo.getService(ILogger.class);
        if (logger == null) {
            throw new KernelException("The logger service must be provided - {}", ILogger.class.getName());
        }

        // Retrieve the configuration service
        // TODO: configuration

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));

        System.exit(0);
    }

    private static final class ShutdownHook implements Runnable {

        @Override
        public void run() {
            System.out.println("The system is going to shutdown...");
            semaphore.release();
        }
    }
}
