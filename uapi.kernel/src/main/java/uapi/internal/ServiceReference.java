package uapi.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uapi.KernelException;

class ServiceReference {

    private final Object    _svc;
    private final Method    _method;

    ServiceReference(Object service, Method method) {
        this._svc = service;
        this._method = method;
    }

    Object invoke(Object... args) {
        Object rtn = null;
        try {
            rtn = this._method.invoke(this._svc, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new KernelException(e, "Invoke method {} on service {} failed",
                    this._method.getName(), this._svc.getClass().getName());
        }
        return rtn;
    }
}
