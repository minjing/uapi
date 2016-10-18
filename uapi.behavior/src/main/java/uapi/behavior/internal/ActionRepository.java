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
//        List<IAction> matchedActions = Looper.from(this._actions)
//                .filter(action -> action.name().equals(name))
//                .toList();
//        if (matchedActions.size() == 0) {
//            return null;
//        } else if (matchedActions.size() == 1) {
//            return matchedActions.get(0);
//        } else {
//            throw new KernelException("Found more action is mapped to name - {}", name);
//        }
    }
}
