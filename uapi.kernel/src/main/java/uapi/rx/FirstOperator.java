/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

/**
 * The FirstOperator will emit only one first data, all subsequent data will be abandoned
 */
class FirstOperator<T> extends TerminatedOperator<T> {

    private boolean _firstIsSent = false;

    FirstOperator(Operator<T> previously) {
        super(previously);
    }

    @Override
    boolean hasItem() {
        if (this._firstIsSent) {
            return false;
        }
        return true;
    }

    @Override
    T getItem() {
        if (! hasItem()) {
            return null;
        }
        this._firstIsSent = true;
        return (T) getPreviously().getItem();
    }
}
