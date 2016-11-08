package uapi.behavior.internal;

import uapi.behavior.IExecutionContext;
import uapi.behavior.Scope;
import uapi.event.IEvent;
import uapi.event.IEventBus;
import uapi.helper.ArgumentChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of IExecutionContext
 */
public final class ExecutionContext implements IExecutionContext {

    private final IEventBus _eventBus;
    private final Map<Object, Object> _globalData;
    private final Map<Object, Object> _data;

    public ExecutionContext(final IEventBus eventBus) {
        ArgumentChecker.required(eventBus, "eventBus");
        this._eventBus = eventBus;
        this._globalData = new ConcurrentHashMap<>();
        this._data = new HashMap<>();
    }

    @Override
    public void fire(IEvent event) {
        fire(event, false);
    }

    @Override
    public void fire(IEvent event, boolean syncable) {
        BehaviorEvent bEvent = new BehaviorEvent(event, this._globalData);
        this._eventBus.fire(bEvent, syncable);
    }

    @Override
    public void put(Object key, Object value, Scope scope) {
        ArgumentChecker.required(key, "key");
        ArgumentChecker.required(scope, "scope");

        if (scope == Scope.Behavior) {
            this._data.put(key, value);
        } else {
            this._globalData.put(key, value);
        }
    }

    @Override
    public void put(Map data, Scope scope) {
        ArgumentChecker.required(data, "data");
        ArgumentChecker.required(scope, "scope");

        if (scope == Scope.Behavior) {
            this._data.putAll(data);
        } else {
            this._globalData.putAll(data);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key) {
        ArgumentChecker.required(key, "key");

        Object value;
        if (this._data.containsKey(key)) {
            value = this._data.get(key);
        } else {
            value = this._globalData.get(key);
        }
        return (T) value;
    }
}
