package uapi.helper;

public class ChangeableBoolean {

    private boolean _value;

    public void set(boolean value) {
        this._value = value;
    }

    public boolean get() {
        return this._value;
    }
}
