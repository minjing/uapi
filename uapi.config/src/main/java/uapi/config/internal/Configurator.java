package uapi.config.internal;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import uapi.config.IConfigurable;
import uapi.helper.ArgumentChecker;
import uapi.service.IServiceReference;
import uapi.service.IWatcher;
import uapi.service.annotation.Service;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A Configurator manage all configuration and configurable service list and
 * set configuration into realted configurable service.
 */
@Service({ "uapi.service.IWatcher" })
class Configurator implements IWatcher {

    private final Multimap<String, WeakReference<IConfigurable>> _configurables;
    private final Map<String, Map<?, ?>> _configuration;

    Configurator() {
        this._configurables = LinkedListMultimap.create();
        this._configuration = new HashMap<>();
    }

    @Override
    public void onRegister(
            final IServiceReference serviceRef
    ) { /* Do nothing */ }

    @Override
    public void onResolved(
            final IServiceReference serviceRef
    ) {
        ArgumentChecker.notNull(serviceRef, "serviceRef");
        String id = serviceRef.getId();
        Object svc = serviceRef.getService();
        if (svc instanceof IConfigurable) {
            this._configurables.put(id, new WeakReference<>((IConfigurable) svc));
        }
    }
}
