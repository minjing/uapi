package uapi.service.internal;

import com.google.auto.service.AutoService;
import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.service.IRegistry;
import uapi.service.IService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Implementation of IRegistry
 */
@AutoService(IService.class)
public class Registry implements IRegistry, IService {

    private final Map<String, ServiceHolder> _satisfiedSvcs;
    private final Map<String, ServiceHolder> _unsatisfiedSvcs;

    public Registry() {
        this._satisfiedSvcs = new HashMap<>();
        this._unsatisfiedSvcs = new HashMap<>();
    }

    @Override
    public String[] getIds() {
        return new String[] { IRegistry.class.getCanonicalName() };
    }

    @Override
    public String[] getDependentIds() {
        return new String[0];
    }

    @Override
    public void register(
            final IService service
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(service, "service");
        registerService(service);
    }

    @Override
    public void register(
            final Object object,
            final String... serviceIds
    ) throws InvalidArgumentException {
        ArgumentChecker.notNull(object, "object");
        ArgumentChecker.notEmpty(serviceIds, "serviceIds");
        register(object, serviceIds);
    }

    private void registerService(
            final Object svc,
            final String[] svcIds) {
        Stream.of(svcIds).forEach(
                svcId -> this._satisfiedSvcs.put(svcId, new ServiceHolder(svc)));
        resolveService(svc);
    }

    private void registerService(
            final IService svc) {
        String[] svcIds = svc.getIds();
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }
        String[] dependencyIds = svc.getDependentIds();
        Stream.of(svcIds).forEach(svcId -> {
            if (dependencyIds == null || dependencyIds.length == 0) {
                this._satisfiedSvcs.put(svcId, new ServiceHolder(svc));
                resolveService(svc);
            } else {
                this._unsatisfiedSvcs.put(svcId, new ServiceHolder(svc));
            }
        });
    }

    private void resolveService(final Object svc) {
        this._unsatisfiedSvcs.forEach((svcId, svcHolder) -> {

        });
    }

    private static final class ServiceHolder {

        final Object _svc;
        final Map<String, Object> _dependencies;

        ServiceHolder(final Object service) {
            this._svc = service;
            this._dependencies = new HashMap<>();
        }

        void addDependency(String serviceId) {
            this._dependencies.put(serviceId, null);
        }
    }
}
