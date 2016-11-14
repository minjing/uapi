package uapi.sample.behavior;

import uapi.app.IAppLifecycle;
import uapi.behavior.IResponsible;
import uapi.log.ILogger;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Application for behavior demo
 */
@Service(IAppLifecycle.class)
@Tag("Behavior Demo")
public class BehaviorDemoApplication implements IAppLifecycle {

    @Inject
    protected ILogger _logger;

    @Inject
    protected List<IResponsible> _responsiblers = new ArrayList<>();

    @Override
    public String getAppName() {
        return "Behavior Demo";
    }

    @Override
    public void onStarted() {
        this._logger.info("Behavior demo is starting...");
        assert this._responsiblers.size() == 2;
        View view = null;
        Store store = null;
        for (IResponsible responsible : this._responsiblers) {
            if (responsible instanceof View) {
                view = (View) responsible;
            } else if (responsible instanceof Store) {
                store = (Store) responsible;
            }
        }
        assert view != null;
        assert store != null;

        view.incCounter();
    }

    @Override
    public void onStopped() {

    }
}
