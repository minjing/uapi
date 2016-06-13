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
 * A MapOperator convert a input item to output item by specific functionality
 */
class MapOperator<I, T> extends Operator<T> {

    private final Functionals.Convert<I, T> _converter;

    MapOperator(Operator<I> previously, Functionals.Convert<I, T> converter) {
        super(previously);
        ArgumentChecker.required(converter, "converter");
        this._converter = converter;
    }

    @Override
    T getItem() {
        I item = ((Operator<I>) getPreviously()).getItem();
        if (item == null) {
            return null;
        }
        return this._converter.accept(item);
    }

    @Override
    void done() {
        // do nothing
    }
}
