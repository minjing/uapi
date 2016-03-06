package uapi.app.internal;

import rx.Observable;
import uapi.KernelException;
import uapi.log.ILogger;
import uapi.service.IRegistry;
import uapi.service.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Semaphore;

/**
 * Created by min on 16/3/6.
 */
public class Launcher {

    private static final Semaphore semaphore;

    static {
        semaphore = new Semaphore(1);
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ServiceLoader<IService> svcLoaders = ServiceLoader.load(IService.class);
        List<IRegistry> svcRegistries = new ArrayList<>();
        List<IService> svcs = new ArrayList<>();
        Observable.from(svcLoaders)
                .doOnNext(svcs::add)
                .filter(svc -> svc instanceof IRegistry)
                .subscribe(svc -> svcRegistries.add((IRegistry) svc));
        if (svcRegistries.size() == 0) {
            throw new KernelException("A IRegistry must be provided");
        }
        if (svcRegistries.size() > 1) {
            throw new KernelException("Found multiple IRegistry instance {}", svcRegistries);
        }
        IRegistry svcRegistry = svcRegistries.get(0);
        svcRegistry.register(svcs);

//        try {
//            Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(logger)));
//            semaphore.acquire();
//        } catch (InterruptedException e) {
//            logger.warn("Encounter an InterruptedException when acquire the semaphore, system will exit.");
//        }

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
