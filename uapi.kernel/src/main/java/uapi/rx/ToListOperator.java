/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

import java.util.LinkedList;
import java.util.List;

/**
 * The ToListOperator will collect all of element and put into a List
 */
class ToListOperator<T> extends TerminatedOperator<List<T>> {

    private List<T> _list;

    ToListOperator(Operator<T> previously) {
        super(previously);
    }

    @Override
    List<T> getItem() {
        if (this._list == null) {
            this._list = new LinkedList<>();
        }
        while (hasItem()) {
            try {
                T item = (T) getPreviously().getItem();
                this._list.add(item);
            } catch (NoItemException ex) {
                // do nothing
            }
        }
        List<T> result = this._list;
        this._list = null;
        return result;
    }
}
