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

import java.util.Iterator;

/**
 * Created by xquan on 6/29/2016.
 */
class IteratorSource<T> extends Operator<T> {

    private final Iterator<T> _itemsIte;

    IteratorSource(Iterator<T> iterator) {
        ArgumentChecker.required(iterator, "iterator");
        this._itemsIte = iterator;
    }

    @Override
    boolean hasItem() {
        return this._itemsIte.hasNext();
    }

    @Override
    T getItem() {
        if (hasItem()) {
            return this._itemsIte.next();
        }
        return null;
    }

    @Override
    void done() {
        // do nothing
    }
}
