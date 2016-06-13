/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

import uapi.InvalidArgumentException;

/**
 * The LimitOperator will limit data by specific count setting
 */
class LimitOperator<T> extends Operator<T> {

    private final int _limitCount;
    private int _count = 0;

    LimitOperator(Operator<T> previously, int limitCount) {
        super(previously);
        if (limitCount < 0) {
            throw new InvalidArgumentException("The argument limitCount must not be a negative");
        }
        this._limitCount = limitCount;
    }

    @Override
    boolean hasItem() {
        if (this._count >= this._limitCount) {
            return false;
        }
        return super.hasItem();
    }

    @Override
    T getItem() {
        if (! hasItem()) {
            return null;
        }
        this._count++;
        return (T) getPreviously().getItem();
    }

    @Override
    void done() {
        this._count = 0;
    }
}
