package uapi.helper;

import uapi.InvalidArgumentException;

/**
 * Created by xquan on 5/25/2016.
 */
public class Triple<LT, CT, RT> {

    private final LT _lValue;

    private final CT _cValue;

    private final RT _rValue;

    /**
     * Split a string by specific separator and using it to construct a Pair instance
     *
     * @param   combined
     *          The combined string which used to split
     * @param   separator
     *          The separator
     * @return  A Pair instance
     */
    public static Triple<String, String, String> splitTo(String combined, String separator) {
        ArgumentChecker.notEmpty(combined, "combined");
        ArgumentChecker.notEmpty(separator, "separator");
        String[] split = combined.split(separator);
        if (split.length == 1) {
            return new Triple<>(split[0], StringHelper.EMPTY, StringHelper.EMPTY);
        } else if (split.length == 2) {
            return new Triple<>(split[0], split[1], StringHelper.EMPTY);
        } else if (split.length == 3) {
            return new Triple<>(split[0], split[1], split[2]);
        } else {
            throw new InvalidArgumentException(
                    "The argument {} can not be separated to one or two value by separator {}",
                    combined, separator);
        }
    }

    public Triple(LT leftValue, CT centerValue, RT rightValue) {
        ArgumentChecker.notNull(leftValue, "leftValue");
        ArgumentChecker.notNull(centerValue, "centerValue");
        ArgumentChecker.notNull(rightValue, "rightValue");
        this._lValue = leftValue;
        this._cValue = centerValue;
        this._rValue = rightValue;
    }

    public LT getLeftValue() {
        return this._lValue;
    }

    public CT getCenterValue() {
        return this._cValue;
    }

    public RT getRightValue() {
        return this._rValue;
    }
}
