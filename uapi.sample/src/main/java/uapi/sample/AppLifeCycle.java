package uapi.sample;

import uapi.app.IAppLifecycle;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.IHttpServer;

/**
 * Created by xquan on 4/5/2016.
 */
@Service({ IAppLifecycle.class })
public class AppLifeCycle implements IAppLifecycle {

    @Inject
    IRegistry _registry;

    private IHttpServer _httpSvr;

    @Override
    public void onStarted() {
        ConfigurableService svc = this._registry.findService(ConfigurableService.class);
        this._httpSvr = this._registry.findService(IHttpServer.class);
        this._httpSvr.start();
    }

    @Override
    public void onStopped() {
        this._httpSvr.stop();
    }
}
