/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.KernelException;
import uapi.app.IAppLifecycle;
import uapi.app.ILauncher;
import uapi.config.ICliConfigProvider;
import uapi.config.annotation.Config;
import uapi.helper.TimeHelper;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.IRegistry;
import uapi.service.IService;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Semaphore;

/**
 * The UAPI application entry point
 */
@Service
@Tag("Launcher")
public class Launcher implements ILauncher {

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
        svcRegistry.register(svcs.toArray(new IService[svcs.size()]));
        svcRegistry = svcRegistry.findService(IRegistry.class);

        ICliConfigProvider cliCfgProvider = svcRegistry.findService(ICliConfigProvider.class);
        cliCfgProvider.parse(args);

        svcRegistry.start();

        Launcher launcher = svcRegistry.findService(Launcher.class);
        launcher.launch(startTime);
    }

    @Inject
    IRegistry _registry;

    @Inject
    ILogger _logger;

    @Inject
    @Optional
    List<IAppLifecycle> _lifecycles;

    @Config(path="app.name", optional=true)
    String _appName;

    private final Semaphore _semaphore;

    public Launcher() {
        this._lifecycles = new ArrayList<>();
        this._semaphore = new Semaphore(0);
    }

    public void stop() {
        this._logger.info("The system is going to shutdown by system...");
        this._semaphore.release();
    }

    @Override
    public void launch(long startTime) {
        if (Strings.isNullOrEmpty(this._appName)) {
            Looper.from(this._lifecycles).foreach(IAppLifecycle::onStarted);
//            Observable.from(this._lifecycles).subscribe(IAppLifecycle::onStarted);
        } else {
            IAppLifecycle appLifecycle = Looper.from(this._lifecycles)
                    .filter(lifecycle -> lifecycle.getAppName().equals(this._appName))
                    .first(null);
            if (appLifecycle != null) {
                appLifecycle.onStarted();
            }
        }

        long expend = System.currentTimeMillis() - startTime;
        long expendSecond = expend / TimeHelper.MS_OF_SECOND;
        long expendMs = expend - (expend / TimeHelper.MS_OF_SECOND);

        this._logger.info("System launched, expend {}.{}s", expendSecond, expendMs);
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));
            this._semaphore.acquire();
        } catch (InterruptedException e) {
            this._logger.warn("Encounter an InterruptedException when acquire the semaphore, system will exit.");
        }

        if (Strings.isNullOrEmpty(this._appName)) {
            Observable.from(this._lifecycles).subscribe(IAppLifecycle::onStopped);
        } else {
            IAppLifecycle appLifecycle = Looper.from(this._lifecycles)
                    .filter(lifecycle -> lifecycle.getAppName().equals(this._appName))
                    .first(null);
            if (appLifecycle != null) {
                appLifecycle.onStopped();
            }
        }
        this._logger.info("The system is shutdown.");
    }

    private final class ShutdownHook implements Runnable {

        @Override
        public void run() {
            Launcher.this._logger.info("The system is going to shutdown by user ...");
            Launcher.this._semaphore.release();
        }
    }
}
