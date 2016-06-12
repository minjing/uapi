/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

import uapi.helper.ArgumentChecker;
import uapi.helper.Functionals;

/**
 * Created by min on 16/6/12.
 */
abstract class Operator<T> implements IOperator<T> {

    private final Operator<T> _previously;
    private boolean _done = false;

    Operator() {
        this._previously = null;
    }

    Operator(Operator previously) {
        ArgumentChecker.required(previously, "previously");
        this._previously = previously;
    }

    Operator<T> getPreviously() {
        return this._previously;
    }

    boolean hasItem() {
        if (this._previously != null) {
            return this._previously.hasItem();
        }
        return false;
    }

    abstract  T getItem();

    void done() {
        this._done = true;
    }

    @Override
    public <O> IOperator<O> map(Functionals.Convert<T, O> operator) {
        return new MapOperator<>(this, operator);
    }

    @Override
    public IOperator<T> filter(Functionals.Filter<T> operator) {
        return new FilterOperator<>(this, operator);
    }

    @Override
    public void foreach(Functionals.Action<T> action) {
        Operator operator = new ForeachOperator(this, action);
        operator.getItem();
    }
}
