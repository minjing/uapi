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

import java.util.Arrays;
import java.util.List;

/**
 * The source hold item list and a position to point to last read position.
 */
class OrderedSource<T> extends Operator<T> {

    private final List<T> _items;
    private int _pos = -1;

    OrderedSource(final List<T> items) {
        ArgumentChecker.required(items, "items");
        this._items = items;
    }

    OrderedSource(final T... items) {
        ArgumentChecker.required(items, "items");
        this._items = Arrays.asList(items);
    }

    @Override
    boolean hasItem() {
        return this._pos < this._items.size() - 1;
    }

    @Override
    T getItem() {
        if (hasItem()) {
            this._pos++;
            return this._items.get(this._pos);
        }
        return null;
    }
}
