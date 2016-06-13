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

    private final List<T> _list;

    ToListOperator(Operator<T> previously) {
        super(previously);
        this._list = new LinkedList<>();
    }

    @Override
    List<T> getItem() {
        while (hasItem()) {
            T item = (T) getPreviously().getItem();
            this._list.add(item);
        }
        return this._list;
    }

    @Override
    void done() {
        this._list.clear();
        super.done();
    }
}
