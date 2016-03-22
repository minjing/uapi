package uapi.config.internal;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import uapi.config.IConfigurable;
import uapi.helper.ArgumentChecker;
import uapi.service.IServiceReference;
import uapi.service.IWatcher;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 3/22/2016.
 */
@Service
class Configurator implements IWatcher {

    private final Multimap<String, IConfigurable> _configs;

    Configurator() {
        this._configs = LinkedListMultimap.create();
    }

    @Override
    public void onRegister(
            final IServiceReference serviceRef
    ) {
        // Do nothing
    }

    @Override
    public void onResolved(
            final IServiceReference serviceRef
    ) {
        ArgumentChecker.notNull(serviceRef, "serviceRef");
        String id = serviceRef.getId();
        Object svc = serviceRef.getService();
        if (svc instanceof IConfigurable) {

        }
    }
}
