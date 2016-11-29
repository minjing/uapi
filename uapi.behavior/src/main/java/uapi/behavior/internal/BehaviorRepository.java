package uapi.behavior.internal;

import uapi.KernelException;
import uapi.behavior.IAction;
import uapi.behavior.IBehavior;
import uapi.behavior.IBehaviorRepository;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The repository used to hold all public behaviors which can be reused
 * in other behavior.
 */
@Service(IBehaviorRepository.class)
@Tag("Behavior")
public class BehaviorRepository implements IBehaviorRepository {

    @Inject
    @Optional
    protected Map<String, IAction> _actions = new ConcurrentHashMap<>();

    @Inject
    @Optional
    protected Map<String, IBehavior> _behaviors = new ConcurrentHashMap<>();

    @Override
    public void register(IBehavior behavior) {
        ArgumentChecker.required(behavior, "behavior");
        String name = behavior.name();
        if (this._behaviors.containsKey(name)) {
            throw new KernelException("A behavior has been bind wit name - {}", name);
        } else {
            this._behaviors.put(name, behavior);
        }
    }

    @Override
    public void register(IAction action) {
        ArgumentChecker.required(action, "action");
        String name = action.name();
        if (this._actions.containsKey(name)) {
            throw new KernelException("An action has been bind with name - {}", name);
        } else {
            this._actions.put(name, action);
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
