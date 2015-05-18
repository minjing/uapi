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
