package uapi.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.InvalidArgumentException.InvalidArgumentType;

class AnnotationServiceMethod {

    private final Object        _svcInst;
    private final Method        _method;
    private final Annotation    _annotation;

    AnnotationServiceMethod(Object serviceInstance, Method method, Annotation annotation) {
        if (serviceInstance == null) {
            throw new InvalidArgumentException("serviceInstance", InvalidArgumentType.EMPTY);
        }
        if (method == null) {
            throw new InvalidArgumentException("method", InvalidArgumentType.EMPTY);
        }
        if (annotation == null) {
            throw new InvalidArgumentException("annotation", InvalidArgumentType.EMPTY);
        }
        this._svcInst = serviceInstance;
        this._method = method;
        this._annotation = annotation;
    }

    Object getServiceInstance() {
        return this._svcInst;
    }

    Method getMethod() {
        return this._method;
    }

    @SuppressWarnings("unchecked")
    <T extends Annotation> T getAnnotation() {
        return (T) this._annotation;
    }

    void invoke(Object... args) {
        try {
            this._method.invoke(this._svcInst, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new KernelException(ex, "Inject config data for service {}:{} failed.",
                    this._svcInst.getClass().getName(), this._method.getName());
        }
    }
}
