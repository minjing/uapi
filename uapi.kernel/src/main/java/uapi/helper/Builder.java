package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.KernelException;

import java.util.HashMap;
import java.util.Map;

/**
 * A Builder used to initialize some properties, validate properties and create an
 * instance
 *
 * @param   <T>
 *          The type of built instance
 */
public abstract class Builder<T> {

    private boolean _built = false;
    private Map<String, Object> _transiences = new HashMap<>();

    /**
     * Put a transience object into the builder.
     * A transience object will be removed after the instance is created which means
     * the transience object is only used by instance creation
     *
     * @param   name
     *          The transience object name
     * @param   object
     *          The transience object
     */
    public void putTransience(final String name, final Object object) {
        ArgumentChecker.notEmpty(name, "name");
        this._transiences.put(name, object);
    }

    /**
     * Receive previously saved transience object by its name
     *
     * @param   name
     *          The name of transience object
     * @param   <E>
     *          The type of transience object
     * @return  The transience object or null
     */
    @SuppressWarnings("unchecked")
    public <E> E getTransience(final String name) {
        ArgumentChecker.notEmpty(name, "name");
        return (E) this._transiences.get(name);
    }

    /**
     * Build instance by currently properties setting
     *
     * @return  The instance
     * @throws  KernelException
     *          Validation failed
     */
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
