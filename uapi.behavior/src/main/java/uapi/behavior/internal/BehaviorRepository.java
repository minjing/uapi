package uapi.behavior.internal;

import uapi.KernelException;
import uapi.behavior.IBehavior;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The repository used to hold all public behaviors which can be reused
 * in other behavior.
 */
@Service
public class BehaviorRepository {

    private Map<String, IBehavior> _behaviors = new ConcurrentHashMap<>();

    public void register(IBehavior behavior) {
        ArgumentChecker.required(behavior, "behavior");
        String name = behavior.name();
        if (this._behaviors.containsKey(name)) {
            throw new KernelException("A behavior has been bind on name - {}", name);
        } else {
            this._behaviors.put(name, behavior);
        }
    }

    public IBehavior findBehavior(String name) {
        ArgumentChecker.required(name, "name");
        return this._behaviors.get(name);
    }
}
