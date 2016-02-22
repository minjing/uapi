package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.KernelException;

public abstract class Builder<T> {

    private boolean _built = false;

    public T build() throws KernelException {
        checkStatus();
        validation();
        initProperties();
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
