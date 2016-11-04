package uapi.behavior.internal;

import uapi.KernelException;
import uapi.behavior.IAction;
import uapi.behavior.IBehavior;
import uapi.behavior.IBehaviorRepository;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The repository used to hold all public behaviors which can be reused
 * in other behavior.
 */
@Service
public class BehaviorRepository implements IBehaviorRepository {

    @Inject
    protected Map<String, IAction> _actions = new ConcurrentHashMap<>();

    private Map<String, IBehavior> _behaviors = new ConcurrentHashMap<>();

    @Override
    public void register(IBehavior behavior) {
        ArgumentChecker.required(behavior, "behavior");
        String name = behavior.name();
        if (this._behaviors.containsKey(name)) {
            throw new KernelException("A behavior has been bind on name - {}", name);
        } else {
            this._behaviors.put(name, behavior);
        }
    }

    @Override
    public IAction find(final String name) {
        ArgumentChecker.required(name, "name");
        String[] actionFrom = name.split("@");
        IAction action;
        if (actionFrom.length == 1) {
            action = this._actions.get(actionFrom[0]);
            if (action == null) {
                action = this._behaviors.get(actionFrom[0]);
            }
        } else if (actionFrom.length == 2) {
            if ("Action".equalsIgnoreCase(actionFrom[1])) {
                action = this._actions.get(actionFrom[0]);
            } else if ("Behavior".equalsIgnoreCase(actionFrom[1])) {
                action = this._behaviors.get(actionFrom[0]);
            } else {
                throw new KernelException("Unsupported action from string - {}", actionFrom[1]);
            }
        } else {
            throw new KernelException("Found more than 1 @ in the action name string - {}", name);
        }
        return action;
    }
}
