package uapi.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.InvalidArgumentException.InvalidArgumentType;

final class ConfigurableServiceDescriptor {

    private final Object    _svc;
    private final Method    _setter;

    ConfigurableServiceDescriptor(Object service, Method setter) {
        if (service == null) {
            throw new InvalidArgumentException("service", InvalidArgumentType.EMPTY);
        }
        if (setter == null) {
            throw new InvalidArgumentException("setter", InvalidArgumentType.EMPTY);
        }
        this._svc = service;
        this._setter = setter;
    }

    void setConfig(Object oldConfig, Object newConfig) {
        try {
            this._setter.invoke(this._svc, oldConfig, newConfig);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new KernelException(ex, "Inject config data for service {}:{} failed.",
                    this._svc.getClass().getName(), this._setter.getName());
        }
    }
}
