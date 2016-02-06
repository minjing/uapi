package uapi.injector;

import uapi.service.IServiceRegistry;

import java.util.List;

/**
 * The interface present its can inject service each other
 * which based on IInjectable interface
 */
public interface IInjector {

    void inject(List<IInjectable> services, IServiceRegistry serviceRegistry);
}
