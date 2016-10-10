package uapi.sample.event;

import uapi.app.IAppLifecycle;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * Application for event demo
 */
@Service(IAppLifecycle.class)
@Tag("Event Demo")
public class EventDemoApplication implements IAppLifecycle {

    @Inject
    protected EventSource _eventSrc;

    @Inject
    protected ILogger _logger;

    @Override
    public String getAppName() {
        return "Event Demo";
    }

    @Override
    public void onStarted() {
        this._logger.info("Event Demo application is started...");
        this._eventSrc.riseEvent();
    }

    @Override
    public void onStopped() {
        this._logger.info("Event Demo application is stopped...");
    }
}
