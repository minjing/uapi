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
 * The SingleOperator return only one item or throw an exception if no item can be returned
 */
class SingleOperator<T> extends TerminatedOperator<T> {

    private boolean _itemSet = false;
    private T _item = null;
    private boolean _useDefault = false;
    private T _default = null;

    SingleOperator(Operator previously) {
        super(previously);
    }

    SingleOperator(Operator<T> previously, T defaultValue) {
        super(previously);
        this._useDefault = true;
        this._default = defaultValue;
    }

    @Override
    T getItem() throws NoItemException {
        boolean hasItem = hasItem();
        while (hasItem) {
            if (this._itemSet) {
                throw new MoreItemException();
            }
            try {
                this._item = (T) getPreviously().getItem();
                this._itemSet = true;
            } catch (NoItemException ex) {
                if (this._itemSet) {
                    return this._item;
                }
                if (this._useDefault) {
                    return this._default;
                } else {
                    throw ex;
                }
            }
            hasItem = hasItem();
        }

        if (this._itemSet) {
            return this._item;
        }
        if (this._useDefault) {
            return this._default;
        } else {
            throw new NoItemException();
        }
    }
}
