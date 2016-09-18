/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.rx.Looper;
import uapi.service.IAsyncCallback;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The AsyncService delegate real service to provide async call functionality
 */
class AsyncService implements InvocationHandler {

    private final Object _svc;
    private final IAsyncCallback _callback;
    private final int _expiredTime;
    private final ExecutorService _exeSvc;
    private final Map<String, Pair<Future, ServiceInvoker>> _callFutures;
    private final AtomicInteger _callIdGen;

    AsyncService(
            final Object service,
            final IAsyncCallback callback,
            final int expiredTime,
            final ExecutorService executorService) {
        ArgumentChecker.required(service, "service");
        ArgumentChecker.required(callback, "callback");
        ArgumentChecker.checkInt(expiredTime, "expiredTime", Integer.MIN_VALUE, Integer.MAX_VALUE);
        ArgumentChecker.required(executorService, "executorService");
        this._svc = service;
        this._callback = callback;
        this._expiredTime = expiredTime;
        this._exeSvc = executorService;
        this._callFutures = new ConcurrentHashMap<>();
        this._callIdGen = new AtomicInteger(1);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String callId = Integer.toString(this._callIdGen.getAndIncrement());
        this._callback.calling(callId, method.getName(), args);
        ServiceInvoker svcInvoker = new ServiceInvoker(callId, method, args);
        Future future = this._exeSvc.submit(svcInvoker);
        this._callFutures.put(callId, new Pair<>(future, svcInvoker));
        return null;
    }

    /**
     * Check below futures:
     * * Timed out future -> cancel it
     * * Done future -> remove it
     * * Canceled future -> remove it
     */
    public void CheckCallFutures() {
        Looper.from(this._callFutures.entrySet().iterator())
                .foreach(entry -> {
                    String callId = entry.getKey();
                    Pair<Future, ServiceInvoker> futureSvc = entry.getValue();
                    Future future = futureSvc.getLeftValue();
                    ServiceInvoker svcInvoker = futureSvc.getRightValue();
                    if (future.isDone() || future.isCancelled()) {
                        this._callFutures.remove(callId);
                    } else {
                        long currentTime = System.currentTimeMillis();
                        if (this._expiredTime <= 0) {
                            return;
                        }
                        if (currentTime - svcInvoker._startTime > this._expiredTime) {
                            cancel(callId);
                            this._callback.timedout(callId);
                        }
                    }
                });
    }

    public void cancel() {
        Looper.from(this._callFutures.keySet().iterator()).foreach(this::cancel);
    }

    private void cancel(String callId) {
        ArgumentChecker.required(callId, "callId");
        Pair<Future, ServiceInvoker> futureSvc = this._callFutures.remove(callId);
        if (futureSvc == null) {
            return;
        }
        Future future = futureSvc.getLeftValue();
        if (! future.isDone() || ! future.isCancelled()) {
            future.cancel(true);
        }
    }

    private void done(String callId) {
        ArgumentChecker.required(callId, "callId");
        this._callFutures.remove(callId);
    }

    private class ServiceInvoker implements Runnable {

        private final String _callId;
        private final Method _method;
        private final Object[] _args;
        private final long _startTime;

        private ServiceInvoker(String callId, final Method method, final Object[] args) {
            this._callId = callId;
            this._method = method;
            this._args = args;
            this._startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            try {
                Object result = this._method.invoke(AsyncService.this._svc, this._args);
                AsyncService.this._callback.succeed(this._callId, result);
            } catch (Exception ex) {
                AsyncService.this._callback.failed(this._callId, ex);
            } finally {
                AsyncService.this._callFutures.remove(this._callId);
                done(this._callId);
            }
        }
    }
}
