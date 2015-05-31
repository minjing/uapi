package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

public class Pair<LT, RT> {

    private final LT _lValue;
    private final RT _rValue;

    public Pair(LT leftValue, RT rightValue) {
        if (leftValue == null) {
            throw new InvalidArgumentException("leftValue", InvalidArgumentType.EMPTY);
        }
        if (rightValue == null) {
            throw new InvalidArgumentException("rightValue", InvalidArgumentType.EMPTY);
        }
        this._lValue = leftValue;
        this._rValue = rightValue;
    }

    public LT getLeftValue() {
        return this._lValue;
    }

    public RT getRightValue() {
        return this._rValue;
    }
}
