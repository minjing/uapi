package uapi.service.internal;

import uapi.InvalidArgumentException;
import uapi.service.IRegistry;
import uapi.service.IService;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of IRegistry
 */
public class Registry implements IRegistry, IService {

    private final Map<String, ServiceHolder> _svcMap;

    public Registry() {
        this._svcMap = new HashMap<>();
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

    }

    private static final class ServiceHolder {

        IService service;
        Map<String, IService> dependencies;
    }
}
