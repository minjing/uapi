package uapi.injector;

import uapi.IService;
import uapi.KernelException;
import uapi.service.IServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by min on 16/2/6.
 */
public final class Injector implements IInjector {

    @Override
    public void resolve(List<IInjectable> services, IServiceRegistry serviceRegistry) {
        ArrayList<UnresolvedService> unresolvedServices = new ArrayList<>();
        services.forEach(service -> {
            UnresolvedService unresolvedService = resolve(service, serviceRegistry);
            if (unresolvedService != null) {
                unresolvedServices.add(unresolvedService);
            } else {
                checkUnresolvedServices(service, unresolvedServices, serviceRegistry);
            }
        });
        if (! unresolvedServices.isEmpty()) {
            throw new KernelException(
                    "There one or more service can't be resolved - {}", unresolvedServices);
        }
    }

    private void checkUnresolvedServices(
            final IService newResolvedService,
            final List<UnresolvedService> unresolvedServices,
            final IServiceRegistry serviceRegistry) {

        if (unresolvedServices.size() == 0) {
            return;
        }
        String serviceId = newResolvedService.getServiceId();
        List<IService> resolvedServices = new ArrayList<>();
        unresolvedServices.forEach(unresolvedService -> {
            if (unresolvedService.dependencies.contains(serviceId)) {
                unresolvedService.service.inject(newResolvedService);
                unresolvedService.dependencies.remove(serviceId);
                if (unresolvedService.dependencies.isEmpty()) {
                    resolvedServices.add(unresolvedService.service);
                    unresolvedServices.remove(unresolvedService);
                    serviceRegistry.register(unresolvedService.service);
                }
            }
        });
        resolvedServices.forEach(resolvedService -> {
            checkUnresolvedServices(resolvedService, unresolvedServices, serviceRegistry);
        });

    }

    private UnresolvedService resolve(
            final IInjectable service,
            final IServiceRegistry serviceRegistry) {

        List<String> dependencies = service.getDependencies();
        ArrayList<String> unfoundServices = new ArrayList<>();
        dependencies.forEach(dependency -> {
            Object dependentSvc = serviceRegistry.findService(dependency);
            if (dependentSvc == null) {
                unfoundServices.add(dependency);
            } else {
                service.inject(dependentSvc);
            }
        });
        if (unfoundServices.size() == 0) {
            // The service is satisfied
            serviceRegistry.register(service);
            return null;
        } else {
            // The service can't be resolved right now
            UnresolvedService unresolvedService = new UnresolvedService();
            unresolvedService.service = service;
            unresolvedService.dependencies = unfoundServices;
            return unresolvedService;
        }
    }

    private final class UnresolvedService {
        private IInjectable service;
        private ArrayList<String> dependencies;
    }
}
