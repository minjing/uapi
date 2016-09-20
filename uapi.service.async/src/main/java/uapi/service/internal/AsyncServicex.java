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
import uapi.config.IntervalTime;
import uapi.config.internal.IntervalTimeParser;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.async.IAsyncService;
import uapi.service.async.ICallFailed;
import uapi.service.async.ICallSucceed;
import uapi.service.async.ICallTimedOut;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xquan on 9/20/2016.
 */
@Service(IAsyncService.class)
public class AsyncServicex implements IAsyncService {

    @Inject
    ILogger _logger;

    @Config(path="service.async.time-of-check", parser=IntervalTimeParser.class)
    IntervalTime _timeOfCheck;

    private static final String EXECUTOR_THREAD_NAME_PATTERN    = "AsyncServiceExecutor-%d";
    private static final String CHECKER_THREAD_NAME_PATTERN     = "AsyncServiceChecker-%d";

    private final ExecutorService _svcExecutor;
    private final ScheduledExecutorService _svcChecker;
    private final AtomicInteger _callIdGen;
    private final Map<String, CallInfo> _callFutures;

    public AsyncServicex() {
        this._callFutures = new ConcurrentHashMap<>();
        this._svcExecutor = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder().setNameFormat(EXECUTOR_THREAD_NAME_PATTERN).build());
        this._svcChecker = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat(CHECKER_THREAD_NAME_PATTERN).build());
        this._callIdGen = new AtomicInteger(1);
    }

    @Init
    public void init() {
        /**
         * Check below futures:
         * * Timed out future -> cancel it
         * * Done future -> remove it
         * * Canceled future -> remove it
         */
        this._svcChecker.scheduleAtFixedRate(() -> {
            Looper.from(this._callFutures.entrySet().iterator())
                    .foreach(entry -> {
                        String callId = entry.getKey();
                        CallInfo callInfo = entry.getValue();
                        Future future = callInfo.future();
                        if (future.isDone() || future.isCancelled()) {
                            this._callFutures.remove(callId);
                        } else {
                            long currentTime = System.currentTimeMillis();
                            IntervalTime expiredTime = callInfo.expiredTime();
                            if (expiredTime.milliseconds() <= 0) {
                                return;
                            }
                            if (currentTime - callInfo.startTime() > expiredTime.milliseconds()) {
                                cancel(callId);
                                callInfo.timedOut();
                            }
                        }
                    });
        }, 0L, this._timeOfCheck.milliseconds(), TimeUnit.MILLISECONDS);
    }

    @Override
    public String call(final Runnable runnable) {
        return call(runnable, null);
    }

    @Override
    public String call(
            final Runnable runnable,
            final Map<String, Object> options) {
        return call(() -> {
            runnable.run();
            return null;
        }, null, null, null, options);
    }

    @Override
    public String call(
            final Callable callable,
            final ICallSucceed succeedCallback
    ) {
        return call(callable, succeedCallback, null, null, null);
    }

    @Override
    public String call(
            final Callable callable,
            final ICallSucceed succeedCallback,
            final Map<String, Object> options
    ) {
        return call(callable, succeedCallback, null, null, options);
    }

    @Override
    public String call(
            final Callable callable,
            final ICallSucceed succeedCallback,
            final ICallFailed failedCallback
    ) {
        return call(callable, succeedCallback, failedCallback, null, null);
    }

    @Override
    public String call(
            final Callable callable,
            final ICallSucceed succeedCallback,
            final ICallFailed failedCallback,
            final Map<String, Object> options
    ) {
        return call(callable, succeedCallback, failedCallback, null, options);
    }

    @Override
    public String call(
            final Callable callable,
            final ICallSucceed succeedCallback,
            final ICallFailed failedCallback,
            final ICallTimedOut timedOutCallback
    ) {
        return call(callable, succeedCallback, failedCallback, null, null);
    }

    @Override
    public String call(
            final Callable callable,
            final ICallSucceed succeedCallback,
            final ICallFailed failedCallback,
            final ICallTimedOut timedOutCallback,
            final Map<String, Object> options
    ) {
        ArgumentChecker.required(callable, "callable");
        String callId = Integer.toString(this._callIdGen.getAndIncrement());
        Future future = this._svcExecutor.submit(callable);
        this._callFutures.put(callId, new CallInfo(callId, options, future, succeedCallback, failedCallback, timedOutCallback));
        return callId;
    }

    private void cancel(String callId) {
        ArgumentChecker.required(callId, "callId");
        CallInfo callInfo = this._callFutures.remove(callId);
        if (callInfo == null) {
            return;
        }
        Future future = callInfo.future();
        if (! future.isDone() || ! future.isCancelled()) {
            future.cancel(true);
        }
    }

    private void done(String callId) {
        ArgumentChecker.required(callId, "callId");
        this._callFutures.remove(callId);
    }

    private final class CallInfo {
        private final String _callId;
        private final Future _future;
        private final long _startTime;
        private final ICallSucceed _succeedCallback;
        private final ICallFailed _failedCallback;
        private final ICallTimedOut _timedOutCallback;

        private final IntervalTime _expiredTime;

        private CallInfo(
                final String callId,
                final Map<String, Object> options,
                final Future future,
                final ICallSucceed successdCallback,
                final ICallFailed failedCalback,
                final ICallTimedOut timedOutCallback
        ) {
            this._startTime = System.currentTimeMillis();
            this._callId = callId;
            this._future = future;
            this._succeedCallback = successdCallback;
            this._failedCallback = failedCalback;
            this._timedOutCallback = timedOutCallback;
            if (options != null) {
                IntervalTime expiredTime = (IntervalTime) options.get(OPTION_TIME_OUT);
                if (expiredTime == null) {
                    expiredTime = new IntervalTime();
                }
                this._expiredTime = expiredTime;
            } else {
                this._expiredTime = new IntervalTime();
            }
        }

        private String callId() {
            return this._callId;
        }

        private Future future() {
            return this._future;
        }

        private IntervalTime expiredTime() {
            return this._expiredTime;
        }

        private long startTime() {
            return this._startTime;
        }

        private void succeed(Object result) {
            if (this._succeedCallback != null) {
                this._succeedCallback.accept(this._callId, result);
            }
        }

        private void failed(Throwable t) {
            if (this._failedCallback != null) {
                this._failedCallback.accept(this._callId, t);
            }
        }

        private void timedOut() {
            if (this._timedOutCallback != null) {
                this._timedOutCallback.accept(this._callId);
            }
        }
    }
}
