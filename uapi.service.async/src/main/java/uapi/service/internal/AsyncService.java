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
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;
import uapi.service.async.IAsyncService;
import uapi.service.async.ICallFailed;
import uapi.service.async.ICallSucceed;
import uapi.service.async.ICallTimedOut;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The AsyncService used wrap sync service to support async call
 */
@Service(IAsyncService.class)
@Tag("Async")
public class AsyncService implements IAsyncService {

    @Inject
    protected ILogger _logger;

    @Config(path="service.async.time-of-check", parser=IntervalTimeParser.class)
    protected IntervalTime _timeOfCheck;

    private static final String EXECUTOR_THREAD_NAME_PATTERN    = "AsyncServiceExecutor-%d";
    private static final String CHECKER_THREAD_NAME_PATTERN     = "AsyncServiceChecker-%d";

    private final ExecutorService _svcExecutor;
    private final ScheduledExecutorService _svcChecker;
    private final AtomicInteger _callIdGen;
    private final Map<String, CallWrapper> _callWrappers;

    public AsyncService() {
        this._callWrappers = new ConcurrentHashMap<>();
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
            Iterator<Map.Entry<String, CallWrapper>> callWrappersIt = this._callWrappers.entrySet().iterator();
            while (callWrappersIt.hasNext()) {
                Map.Entry<String, CallWrapper> callWrapperEntry = callWrappersIt.next();
                String callId = callWrapperEntry.getKey();
                CallWrapper callWrapper = callWrapperEntry.getValue();
                Future future = callWrapper.future();
                if (future.isDone() || future.isCancelled()) {
                    this._callWrappers.remove(callId);
                } else {
                    IntervalTime expiredTime = callWrapper.expiredTime();
                    if (expiredTime.milliseconds() <= 0) {
                        return;
                    }
                    if (callWrapper.checkExpired()) {
                        callWrappersIt.remove();
                    }
                }
            }
        }, 0L, this._timeOfCheck.milliseconds(), TimeUnit.MILLISECONDS);
    }

    public int callCount() {
        return this._callWrappers.size();
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
        CallWrapper callWrapper = new CallWrapper(
                callId, options, callable, succeedCallback, failedCallback, timedOutCallback);
        callWrapper._future = this._svcExecutor.submit(callWrapper);
        this._callWrappers.put(callId, callWrapper);
        return callId;
    }

    private void done(String callId) {
        ArgumentChecker.required(callId, "callId");
        this._callWrappers.remove(callId);
    }

    private final class CallWrapper implements Runnable {
        private final String _callId;
        private final Callable _callable;
        private final long _startTime;
        private final ICallSucceed _succeedCallback;
        private final ICallFailed _failedCallback;
        private final ICallTimedOut _timedOutCallback;

        private final IntervalTime _expiredTime;

        private volatile CallStatus _status;
        private Future _future;

        private CallWrapper(
                final String callId,
                final Map<String, Object> options,
                final Callable callable,
                final ICallSucceed succeedCallback,
                final ICallFailed failedCallback,
                final ICallTimedOut timedOutCallback
        ) {
            this._startTime = System.currentTimeMillis();
            this._callId = callId;
            this._callable = callable;
            this._succeedCallback = succeedCallback;
            this._failedCallback = failedCallback;
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

        @Override
        public void run() {
            Object result = null;
            Exception exception = null;
            try {
                result = this._callable.call();
                this._status = CallStatus.SUCCEED;
            } catch (Exception ex) {
                if (this._status != CallStatus.TIMEDOUT) {
                    this._status = CallStatus.FAILED;
                    exception = ex;
                }
            }
            try {
                switch (this._status) {
                    case SUCCEED:
                        if (this._succeedCallback != null) {
                            this._succeedCallback.accept(this._callId, result);
                        }
                        break;
                    case FAILED:
                        if (this._failedCallback != null) {
                            this._failedCallback.accept(this._callId, exception);
                        }
                        break;
                    case TIMEDOUT:
                        if (this._timedOutCallback != null) {
                            this._timedOutCallback.accept(this._callId);
                        }
                }
            } catch (Exception ex) {
                AsyncService.this._logger.error(ex);
            } finally {
                done(this._callId);
            }
        }

        private Future future() {
            return this._future;
        }

        private IntervalTime expiredTime() {
            return this._expiredTime;
        }

        private boolean checkExpired() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this._startTime > this._expiredTime.milliseconds()) {
                if (this._future.isDone() || !this._future.isCancelled()) {
                    this._status = CallStatus.TIMEDOUT;
                    this._future.cancel(true);
                }
                return true;
            }
            return false;
        }
    }

    private enum CallStatus {
        SUCCEED, FAILED, TIMEDOUT
    }
}
