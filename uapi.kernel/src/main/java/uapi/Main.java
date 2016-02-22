package uapi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Semaphore;

import uapi.helper.StringHelper;
import uapi.helper.TimeHelper;
import uapi.internal.CliConfigProvider;
import uapi.internal.Service1Repository;
import uapi.log.ILogger;
import uapi.service.IService1;

public final class Main {

    private static final Semaphore semaphore;

    static {
        semaphore = new Semaphore(1);
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ServiceLoader<IService1> svcLoaders = ServiceLoader.load(IService1.class);
        Service1Repository svcRepo = null;
        List<IService1> svcs = new ArrayList<>();
        // find out Service1Repository first and then put all services into it;
        for (IService1 svc : svcLoaders) {
            if (Service1Repository.class.equals(svc.getClass())) {
                svcRepo = (Service1Repository) svc;
            }
            svcs.add(svc);
        }
        if (svcRepo == null) {
            throw new KernelException("Can't find out Service1Repository instance");
        }
        svcRepo.addServices(svcs);

        // Initialize CLI arguments
        CliConfigProvider cliCfgSrc = svcRepo.getService(CliConfigProvider.class);
        cliCfgSrc.parse(args);

        svcRepo = svcRepo.getService(Service1Repository.class);

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
