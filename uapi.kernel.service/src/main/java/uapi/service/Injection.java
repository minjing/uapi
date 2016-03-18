package uapi.service;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;

/**
 * The injection wrap an object which can be injected to other service.
 * The wrap contains all mata information about injected object.
 */
public class Injection {

    private final String _id;
    private final Object _object;

    public Injection(
            final String id,
            final Object object
    ) throws InvalidArgumentException {
        ArgumentChecker.notEmpty(id, "id");
        ArgumentChecker.notNull(object, "object");

        this._id = id;
        this._object = object;
    }

    public String getId() {
        return this._id;
    }

    public Object getObject() {
        return this._object;
    }

    public void checkType(
            final Class type
    ) throws KernelException {
        if (! this._object.getClass().isAssignableFrom(type)) {
            throw new KernelException(
                    "The object type {} can't match its type {}",
                    this._object.getClass(), type);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Injection[")
                .append("id=").append(this._id)
                .append("object=").append(this._object);
        return sb.toString();
    }
}
