package uapi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Semaphore;

import uapi.internal.ServiceRepository;
import uapi.log.ILogger;
import uapi.service.IService;

public final class Main {

    private static final Semaphore semaphore;

    static {
        semaphore = new Semaphore(1);
    }

    public static void main(String[] args) {
        ServiceLoader<IService> svcLoaders = ServiceLoader.load(IService.class);
        ServiceRepository svcRepo = null;
        List<IService> svcs = new ArrayList<>();
        // find out ServiceRepository first and then put all services into it;
        for (IService svc : svcLoaders) {
            if (ServiceRepository.class.equals(svc.getClass())) {
                svcRepo = (ServiceRepository) svc;
            }
            svcs.add(svc);
        }
        if (svcRepo == null) {
            throw new KernelException("Can't find out ServiceRepository instance");
        }
        svcRepo.addServices(svcs);
        svcRepo = svcRepo.getService(ServiceRepository.class);

        // Retrieve the log service
        ILogger logger = svcRepo.getService(ILogger.class);
        if (logger == null) {
            throw new KernelException("The logger service must be provided - {}", ILogger.class.getName());
        }

        // Retrieve the configuration service
        // TODO: configuration

        try {
            Runtime.getRuntime().addShutdownHook(
                    new Thread(new ShutdownHook(logger)));
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.info("Encounter an InterruptedException when acquire the semaphore, system will exit.");
        }

        System.exit(0);
    }

    private static final class ShutdownHook implements Runnable {

        private final ILogger _logger;

        private ShutdownHook(ILogger logger) throws InterruptedException {
            this._logger = logger;
            semaphore.acquire();
        }

        @Override
        public void run() {
            this._logger.info("The system is going to shutdown...");
            semaphore.release();
        }
    }
}
