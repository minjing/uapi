package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

public class Pair<LT, RT> {

    private final LT _lValue;
    private final RT _rValue;

    public static Pair<String, String> splitTo(String combined, String separator) {
        ArgumentChecker.notEmpty(combined, "combined");
        ArgumentChecker.notEmpty(separator, "separator");
        String[] split = combined.split(separator);
        if (split.length == 1) {
            return new Pair<>(split[0], StringHelper.EMPTY);
        } else if (split.length == 2) {
            return new Pair<>(split[0], split[1]);
        } else {
            throw new InvalidArgumentException(
                    "The argument {} can not be separated to one or two value by separator {}",
                    combined, separator);
        }
    }

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
