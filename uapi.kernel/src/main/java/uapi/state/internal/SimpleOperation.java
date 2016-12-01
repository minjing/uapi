package uapi.state.internal;

import uapi.helper.ArgumentChecker;
import uapi.state.IOperation;

/**
 * The simple operation contains operation type only
 */
class SimpleOperation implements IOperation {

    private final String _type;

    SimpleOperation(final String type) {
        ArgumentChecker.required(type, "type");
        this._type = type;
    }

    @Override
    public String type() {
        return this._type;
    }
}
