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

import java.util.*;

/**
 * A CollectionSource hold data source locally, unlike OrderedSource, it does not guarantee the items are ordered
 * which is depends on underlay implementation
 */
class CollectionSource<T> extends Operator<T> {

    private final Collection<T> _items;
    private Iterator<T> _itemsIte;

    CollectionSource(Collection<T> items) {
        ArgumentChecker.required(items, "items");
        this._items = items;
        this._itemsIte = items.iterator();
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
        this._itemsIte = this._items.iterator();
    }
}
