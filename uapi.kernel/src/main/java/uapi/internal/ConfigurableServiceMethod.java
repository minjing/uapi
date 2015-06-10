package uapi.internal;

import java.lang.reflect.Method;

import uapi.KernelException;

final class ConfigurableServiceMethod
    extends AnnotationServiceMethod {

    private Class<?> _cfgType;

    ConfigurableServiceMethod(AnnotationServiceMethod serviceMethod) {
        super(serviceMethod.getServiceInstance(), serviceMethod.getMethod(), serviceMethod.getAnnotation());

        Method method = serviceMethod.getMethod();
        Class<?>[] argTypes = method.getParameterTypes();
        if (argTypes.length != 2) {
            throw new KernelException("The config method {} only allow 2 arguments on service {}.",
                    serviceMethod.getMethod().getName(), serviceMethod.getServiceInstance().getClass().getName());
        }
        if (! argTypes[0].equals(argTypes[1])) {
            throw new KernelException("The type of old/new config type of method {} must be same on service {} .",
                    serviceMethod.getMethod().getName(), serviceMethod.getServiceInstance().getClass().getName());
        }
        this._cfgType = argTypes[0];
    }

    void updateConfig(Object oldConfig, Object newConfig) {
        if (oldConfig != null && ! oldConfig.getClass().equals(newConfig.getClass())) {
            throw new KernelException("The type of old config {} and the type of new config {} are not same.");
        }
        if (! this._cfgType.isAssignableFrom(newConfig.getClass())) {
            throw new KernelException("The type {} cannot be assigned to type {} on method {} on service {}.",
                    oldConfig.getClass().getName(), this._cfgType.getName(), getMethod().getName(), getServiceInstance().getClass().getName());
        }
        invoke(oldConfig, newConfig);
    }
}
