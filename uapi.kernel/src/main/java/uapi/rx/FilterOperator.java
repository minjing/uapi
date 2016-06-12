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
class FilterOperator<T> extends Operator<T>{

    private final Functionals.Filter<T> _filter;

    FilterOperator(Operator<T> previously, final Functionals.Filter<T> filter) {
        super(previously);
        ArgumentChecker.required(filter, "filter");
        this._filter = filter;
    }

    @Override
    T getItem() {
        if (hasItem()) {
            T item = getPreviously().getItem();
            while (item != null) {
                boolean filtered = this._filter.accept(item);
                if (! filtered) {
                    break;
                }
                item = getPreviously().getItem();
            }
            return item;
        }
        return null;
    }
}
