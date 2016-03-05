package uapi.helper;

public class ChangeableBoolean {

    private boolean _value;

    public ChangeableBoolean() {
        this(false);
    }

    public ChangeableBoolean(boolean value) {
        this._value = value;
    }

    public void set(boolean value) {
        this._value = value;
    }

    public boolean get() {
        return this._value;
    }
}
