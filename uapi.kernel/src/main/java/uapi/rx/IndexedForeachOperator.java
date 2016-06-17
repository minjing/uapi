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

/**
 * A IndexedForeachOperator iterate all items by specific functionality with item index
 */
public class IndexedForeachOperator<T> extends TerminatedOperator<T> {

    private final IndexedForeach<T> _action;

    IndexedForeachOperator(Operator<T> previously, final IndexedForeach<T> action) {
        super(previously);
        ArgumentChecker.required(action, "action");
        this._action = action;
    }

    @Override
    T getItem() {
        boolean hasItem = hasItem();
        int index = 0;
        while (hasItem) {
            T item = (T) getPreviously().getItem();
            this._action.accept(index, item);
            hasItem = hasItem();
            index++;
        }
        return null;
    }

    @FunctionalInterface
    public interface IndexedForeach<T> {
        void accept(int index, T in);
    }
}
