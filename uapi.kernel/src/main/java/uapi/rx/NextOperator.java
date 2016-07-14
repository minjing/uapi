/*
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
 * A NextOperator will do specific action on each input item
 */
class NextOperator<T> extends Operator<T> {

    private final Functionals.Action<T> _action;

    NextOperator(Operator<T> previously, final Functionals.Action<T> action) {
        super(previously);
        ArgumentChecker.required(action, "action");
        this._action = action;
    }

    @Override
    T getItem() throws NoItemException {
        T item = ((Operator<T>) getPreviously()).getItem();
//        if (item == null) {
//            return null;
//        }
        this._action.accept(item);
        return item;
    }
}
