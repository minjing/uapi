package uapi.config.internal;

import rx.Observable;
import uapi.config.Configuration;
import uapi.config.IConfigProvider;
import uapi.config.IConfigTracer;
import uapi.config.IConfigurable;
import uapi.helper.ArgumentChecker;
import uapi.service.ISatisfyHook;
import uapi.service.annotation.Init;
import uapi.service.annotation.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * A Configurator manage all configuration and configurable service list and
 * set configuration into related configurable service.
 */
@Service({ ISatisfyHook.class })
class Configurator implements ISatisfyHook {

    private final List<IConfigProvider> _configProviders;
    private final ConfigTracer _configTracer;

//    private final Multimap<String /* config path */, WeakReference<IConfigurable>> _configurables;
    private final Configuration _rootConfig;

    Configurator() {
        this._configProviders = new LinkedList<>();
        this._configTracer = new ConfigTracer();
//        this._configurables = LinkedListMultimap.create();
        this._rootConfig = Configuration.createRoot();
    }

    @Init
    public void init() {
        Observable.from(this._configProviders).subscribe(provider -> provider.setTracer(this._configTracer));
    }

    @Override
    public boolean isSatisfied(Object service) {
        ArgumentChecker.notNull(service, "service");
        if (! (service instanceof IConfigurable)) {
            return true;
        }
        IConfigurable configurableSvc = (IConfigurable) service;
        String[] paths = configurableSvc.getPaths();
        boolean isConfigured = true;
        for (String path : paths) {
            if (! this._rootConfig.bindConfigurable(path, configurableSvc)) {
                isConfigured = false;
            }
        }
        return isConfigured;
    }

    private final class ConfigTracer implements IConfigTracer {

        @Override
        public void onChange(String path, Object config) {
            Configurator.this._rootConfig.setValue(path, config);
        }
    }
}
