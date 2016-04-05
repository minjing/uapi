package uapi.sample;

import uapi.app.IAppLifecycle;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 4/5/2016.
 */
@Service({ IAppLifecycle.class })
public class AppLifeCycle implements IAppLifecycle {

    @Inject
    IRegistry _registry;

    @Override
    public void onStarted() {
        ConfigurableService svc = this._registry.findService(ConfigurableService.class);
    }

    @Override
    public void onStopped() {

    }
}
