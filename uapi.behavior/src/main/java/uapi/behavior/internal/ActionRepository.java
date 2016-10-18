package uapi.behavior.internal;

import uapi.behavior.IAction;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ActionRepository hold all public action
 */
@Service
public class ActionRepository {

    @Inject
    protected Map<String, IAction> _actions = new ConcurrentHashMap<>();

    public IAction findAction(final String name) {
        ArgumentChecker.required(name, "name");
        return this._actions.get(name);
    }
}
