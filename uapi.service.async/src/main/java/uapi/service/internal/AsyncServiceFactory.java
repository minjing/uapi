/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.helper.Guarder;
import uapi.rx.Looper;
import uapi.service.IAsyncCallback;
import uapi.service.IAsyncServiceBuilder;
import uapi.service.IAsyncServiceFactory;
import uapi.service.IService;
import uapi.service.annotation.Init;
import uapi.service.annotation.Service;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The service used to wrap real service to provide async service invocation feature
 */
@Service(IAsyncServiceFactory.class)
public class AsyncServiceFactory implements IAsyncServiceFactory {

    private static final String EXECUTOR_THREAD_NAME_PATTERN    = "AsyncServiceExecutor-%d";
    private static final String CHECKER_THREAD_NAME_PATTERN     = "AsyncServiceChecker-%d";

    private final ExecutorService _svcExecutor;
    private final ScheduledExecutorService _svcChecker;
    private final List<WeakReference<AsyncService>> _asyncSvcs;
    private final Lock _lock;

    /**
     * time of checking, unit is ms
     */
    @Config(path="service.async.time-of-check")
    int _timeOfCheck;

    public AsyncServiceFactory() {
        this._svcExecutor = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder().setNameFormat(EXECUTOR_THREAD_NAME_PATTERN).build());
        this._svcChecker = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat(CHECKER_THREAD_NAME_PATTERN).build());
        this._asyncSvcs = new LinkedList<>();
        this._lock = new ReentrantLock();
    }

    @Init
    public void init() {
        this._svcChecker.scheduleAtFixedRate(() -> {
            Iterator<WeakReference<AsyncService>> it = this._asyncSvcs.iterator();
            while(it.hasNext()) {
                WeakReference<AsyncService> asyncSvcRef = it.next();
                AsyncService asyncSvc = asyncSvcRef.get();
                if (asyncSvc == null) {
                    it.remove();
                    continue;
                }
                asyncSvc.CheckCallFutures();
            }
        }, 0, this._timeOfCheck, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T extends IService> IAsyncServiceBuilder<T> newBuilder(Class<T> serviceType) {
        return new AsyncServiceBuilder<>(serviceType);
    }

    public void onUnload() {
        Guarder.by(this._lock).run(() -> {
            Looper.from(this._asyncSvcs)
                    .filter(asyncSvcRef -> asyncSvcRef.get() != null)
                    .map(WeakReference::get)
                    .foreach(AsyncService::cancel);
        });
    }

    private final class AsyncServiceBuilder<T extends IService> implements IAsyncServiceBuilder<T> {

        private final Class<T> _svcType;
        private T _svc;
        private IAsyncCallback _callback;
        private int _time;

        private AsyncServiceBuilder(final Class<T> serviceType) {
            ArgumentChecker.required(serviceType, "serviceType");
            this._svcType = serviceType;
        }

        @Override
        public IAsyncServiceBuilder<T> on(T service) {
            ArgumentChecker.required(service, "service");
            this._svc = service;
            return this;
        }

        @Override
        public IAsyncServiceBuilder<T> with(IAsyncCallback callback) {
            ArgumentChecker.required(callback, "callback");
            this._callback = callback;
            return this;
        }

        @Override
        public IAsyncServiceBuilder<T> timeout(int time) {
            ArgumentChecker.checkInt(time, "time", 1, Integer.MAX_VALUE);
            this._time = time;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T build() {
            ArgumentChecker.required(this._svcType, "serviceType");
            ArgumentChecker.required(this._callback, "callback");
            AsyncService asyncSvc = new AsyncService(
                    this._svc, this._callback, this._time, AsyncServiceFactory.this._svcExecutor);
            AsyncServiceFactory.this._asyncSvcs.add(new WeakReference(asyncSvc));
            return (T) Proxy.newProxyInstance(
                    this._svcType.getClassLoader(),
                    new Class<?>[] { this._svcType },
                    asyncSvc);
        }
    }
}
