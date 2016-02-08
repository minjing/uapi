package uapi.helper;

import uapi.KernelException;

public abstract class Builder<T> {

    private boolean _built = false;

    public T build() {
        T obj = buildInstance();
        this._built = true;
        return obj;
    }

    protected void checkStatus() {
        if (this._built) {
            throw new KernelException("The builder[{}] is already used", this.getClass().getName());
        }
    }

    protected abstract T buildInstance();
}
