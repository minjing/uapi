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
    private boolean _useDefault = false;
    private T _default = null;

    FirstOperator(Operator<T> previously) {
        super(previously);
    }

    FirstOperator(Operator<T> previously, T defaultValue) {
        super(previously);
        this._useDefault = true;
        this._default = defaultValue;
    }

    @Override
    boolean hasItem() {
        if (this._firstIsSent) {
            return false;
        }
        return super.hasItem();
    }

    @Override
    T getItem() {
        if (! hasItem()) {
            if (this._useDefault) {
                return this._default;
            } else {
                throw new NoItemException();
            }
        }
        this._firstIsSent = true;
        try {
            return (T) getPreviously().getItem();
        } catch (NoItemException ex) {
            if (this._useDefault) {
                return this._default;
            } else {
                throw ex;
            }
        }
    }
}
