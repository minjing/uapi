package uapi.service.internal;

import com.google.auto.service.AutoService;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.service.IRegistry;
import uapi.service.IService;

import java.util.stream.Stream;

/**
 * Implementation of IRegistry
 */
@AutoService(IService.class)
public class Registry implements IRegistry, IService {

    private final Multimap<String, ServiceHolder> _resolvedSvcs;
    private final Multimap<String, ServiceHolder> _unresolvedSvcs;

    public Registry() {
        this._resolvedSvcs = LinkedListMultimap.create();
        this._unresolvedSvcs = LinkedListMultimap.create();
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
        Stream.of(svcIds).map(svcId -> {
            ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
            this._resolvedSvcs.put(svcId, svcHolder);
            return svcHolder;
        }).forEach(svcHolder -> newResolveService(svcHolder));
    }

    private void registerService(
            final IService svc) {
        String[] svcIds = svc.getIds();
        if (svcIds == null || svcIds.length == 0) {
            throw new InvalidArgumentException("The service id is required - {}", svc.getClass().getName());
        }

        String[] dependencyIds = svc.getDependentIds();
        if (dependencyIds == null || dependencyIds.length == 0) {
            Stream.of(svcIds).map(svcId -> {
                ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
                this._resolvedSvcs.put(svcId, svcHolder);
                return svcHolder;
            }).forEach(svcHolder -> newResolveService(svcHolder));

//            Observable.from(svcIds)
//                    .flatMap(svcId -> Observable.from(
//                            this._unresolvedSvcs.values().stream().filter(service -> service.dependsOn(svcId)).collect(Collectors.toList())))
//                    .filter(serviceHolder -> serviceHolder.setDependency(serviceHolder))
//                    .subscribe(serviceHolder -> serviceHolder.setDependency())
        } else {
            Stream.of(svcIds).forEach(svcId -> this._unresolvedSvcs.put(svcId, new ServiceHolder(svc, svcId)));
        }

//        Stream.of(svcIds).forEach(svcId -> {
//            ServiceHolder svcHolder = new ServiceHolder(svc, svcId);
//            if (dependencyIds == null || dependencyIds.length == 0) {
//                this._resolvedSvcs.put(svcId, svcHolder);
//                newResolveService(svcHolder);
//            } else {
//                this._unresolvedSvcs.put(svcId, svcHolder);
//            }
//        });
    }

    private void newResolveService(final ServiceHolder svcHolder) {
//        Observable.from(svcHolders)
//                .map(ServiceHolder::getId)
//                .flatMap(svcId -> Observable.from(
//                        this._unresolvedSvcs.values().stream().filter(service -> service.dependsOn(svcId)).collect(Collectors.toList())))
//                .filter(serviceHolder -> serviceHolder.setDependency(serviceHolder))
//                .subscribe(serviceHolder -> newResolveService())

        final String svcId = svcHolder.getId();
        this._unresolvedSvcs.values().stream()
                .filter(service -> service.dependsOn(svcId))
                .filter(serviceHolder -> serviceHolder.setDependency(serviceHolder))
                .map(serviceHolder -> {
                    this._unresolvedSvcs.remove(svcId, svcHolder);
                    this._resolvedSvcs.put(svcId, svcHolder);
                    return svcHolder;
                })
                .forEach(svcHOlder -> newResolveService(svcHolder));

    }
}
