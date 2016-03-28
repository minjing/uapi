package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.KernelException;

import java.util.HashMap;
import java.util.Map;

public abstract class Builder<T> {

    private boolean _built = false;
    private Map<String, Object> _transiences = new HashMap<>();

    public void putTransience(final String name, final Object object) {
        ArgumentChecker.notEmpty(name, "name");
        this._transiences.put(name, object);
    }

    @SuppressWarnings("unchecked")
    public <E> E getTransience(final String name) {
        ArgumentChecker.notEmpty(name, "name");
        return (E) this._transiences.get(name);
    }

    public T build() throws KernelException {
        checkStatus();
        validation();
        initProperties();
        this._transiences.clear();
        T obj = createInstance();
        this._built = true;
        return obj;
    }

    protected void checkStatus() {
        if (this._built) {
            throw new KernelException("The builder[{}] is already used", this.getClass().getName());
        }
    }

    protected abstract void validation() throws InvalidArgumentException;

    protected abstract void initProperties();

    protected abstract T createInstance();
}
