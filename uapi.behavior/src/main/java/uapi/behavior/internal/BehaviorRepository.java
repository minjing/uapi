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
    public IAction findAction(final String name) {
        ArgumentChecker.required(name, "name");
        return this._actions.get(name);
    }

    @Override
    public IBehavior findBehavior(String name) {
        ArgumentChecker.required(name, "name");
        return this._behaviors.get(name);
    }
}
