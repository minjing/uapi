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

import java.util.Collection;

/**
 * Created by min on 16/6/12.
 */
public class Iterator {

    public static <T> IOperator<T> from(final T... items) {
        ArgumentChecker.required(items, "items");
        return new OrderedSource<>(items);
    }

    public static <T> IOperator<T> from(Collection<T> items) {
        ArgumentChecker.required(items, "items");
        return new CollectionSource<>(items);
    }
}
