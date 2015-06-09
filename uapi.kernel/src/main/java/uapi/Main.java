package uapi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Semaphore;

import uapi.helper.StringHelper;
import uapi.helper.TimeHelper;
import uapi.internal.CliConfigProvider;
import uapi.internal.ServiceRepository;
import uapi.log.ILogger;
import uapi.service.IService;

public final class Main {

    private static final Semaphore semaphore;

    static {
        semaphore = new Semaphore(1);
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

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

        // Initialize CLI arguments
        CliConfigProvider cliCfgSrc = svcRepo.getService(CliConfigProvider.class);
        cliCfgSrc.parse(args);

        // Retrieve the configuration service
        // TODO: configuration

        long expend = System.currentTimeMillis() - startTime;
        long expendSecond = expend / TimeHelper.MS_OF_SECOND;
        long expendMs = expend - (expend / TimeHelper.MS_OF_SECOND);

        ILogger logger = svcRepo.getService(ILogger.class, new Main());
        logger.info(StringHelper.makeString("System launched, expend {}.{}s", expendSecond, expendMs));

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(logger)));
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.warn("Encounter an InterruptedException when acquire the semaphore, system will exit.");
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
