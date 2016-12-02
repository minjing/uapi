package uapi.service.internal;

/**
 * Created by xquan on 12/2/2016.
 */
enum ServiceState {
    Unresolved(0),
    Resolved(10),
    Injected(20),
    Satisfied(30),
    Activated(40),
    Deactivated(50),
    Destroyed(-1);

    private int _value;

    int value() {
        return this._value;
    }

    ServiceState(int value) {
        this._value = value;
    }
}
