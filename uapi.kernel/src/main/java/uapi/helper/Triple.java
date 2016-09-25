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

/**
 * The Triple hold three value
 */
public class Triple<L, C, R> {

    private final L _lValue;

    private final C _cValue;

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

    public Triple(L leftValue, C centerValue, R rightValue) {
        ArgumentChecker.notNull(leftValue, "leftValue");
        ArgumentChecker.notNull(centerValue, "centerValue");
        ArgumentChecker.notNull(rightValue, "rightValue");
        this._lValue = leftValue;
        this._cValue = centerValue;
        this._rValue = rightValue;
    }

    public L getLeftValue() {
        return this._lValue;
    }

    public C getCenterValue() {
        return this._cValue;
    }

    public R getRightValue() {
        return this._rValue;
    }
}
