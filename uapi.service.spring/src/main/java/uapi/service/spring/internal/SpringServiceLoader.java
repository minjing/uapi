package uapi.service.spring.internal;

import uapi.service.IRegistry;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * The service used to load Spring bean into the framework
 */
@Service
public class SpringServiceLoader {

    @Inject
    IRegistry _registry;

    @Init
    public void init() {

    }
}
