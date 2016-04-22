package uapi.config.internal;

import uapi.config.Configuration;
import uapi.config.IConfigTracer;
import uapi.config.IConfigurable;
import uapi.helper.ArgumentChecker;
import uapi.service.ISatisfyHook;
import uapi.service.IServiceReference;
import uapi.service.annotation.Init;
import uapi.service.annotation.Service;

import java.util.Map;

/**
 * A Configurator manage all configuration and configurable service list and
 * set configuration into related configurable service.
 */
@Service({ ISatisfyHook.class, IConfigTracer.class })
class Configurator implements ISatisfyHook, IConfigTracer {

    private final Configuration _rootConfig;

    Configurator() {
        this._rootConfig = Configuration.createRoot();
    }

    @Init
    public void init() {
//        Observable.from(this._configProviders).subscribe(provider -> provider.setTracer(this._configTracer));
    }

    @Override
    public boolean isSatisfied(IServiceReference serviceRef) {
        ArgumentChecker.notNull(serviceRef, "serviceRef");
        if (! (serviceRef.getService() instanceof IConfigurable)) {
            return true;
        }
        IConfigurable configurableSvc = (IConfigurable) serviceRef.getService();
        String[] paths = configurableSvc.getPaths();
        boolean isConfigured = true;
        for (String path : paths) {
            if (! this._rootConfig.bindConfigurable(path, serviceRef)) {
                isConfigured = false;
            }
        }
        return isConfigured;
    }

    @Override
    public void onChange(String path, Object config) {
        Configurator.this._rootConfig.setValue(path, config);
    }

    @Override
    public void onChange(Map<String, Object> configMap) {
        Configurator.this._rootConfig.setValue(configMap);
    }
}
