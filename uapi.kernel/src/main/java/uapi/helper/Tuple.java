/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;

/**
 * A pair contains two values
 *
 * @param   <L>
 *          left value type
 * @param   <R>
 *          right value type
 */
public class Tuple<L, R> {

    private final L _lValue;

    private final R _rValue;

    /**
     * Split a string by specific separator and using it to construct a Pair instance
     *
     * @param   combined
     *          The combined string which used to split
     * @param   separator
     *          The separator
     * @return  A Pair instance
     */
    public static Tuple<String, String> splitTo(String combined, String separator) {
        ArgumentChecker.notEmpty(combined, "combined");
        ArgumentChecker.notEmpty(separator, "separator");
        String[] split = combined.split(separator);
        if (split.length == 1) {
            return new Tuple<>(split[0], StringHelper.EMPTY);
        } else if (split.length == 2) {
            return new Tuple<>(split[0], split[1]);
        } else {
            throw new InvalidArgumentException(
                    "The argument {} can not be separated to one or two value by separator {}",
                    combined, separator);
        }
    }

    public Tuple(L leftValue, R rightValue) {
        if (leftValue == null) {
            throw new InvalidArgumentException("leftValue", InvalidArgumentType.EMPTY);
        }
        if (rightValue == null) {
            throw new InvalidArgumentException("rightValue", InvalidArgumentType.EMPTY);
        }
        this._lValue = leftValue;
        this._rValue = rightValue;
    }

    public L getLeftValue() {
        return this._lValue;
    }

    public R getRightValue() {
        return this._rValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?> pair = (Tuple<?, ?>) o;

        if (!_lValue.equals(pair._lValue)) return false;
        return _rValue.equals(pair._rValue);
    }

    @Override
    public int hashCode() {
        int result = _lValue.hashCode();
        result = 31 * result + _rValue.hashCode();
        return result;
    }
}
