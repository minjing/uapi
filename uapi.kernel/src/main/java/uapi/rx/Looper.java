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
import java.util.Iterator;

/**
 * An Looper will generate IOperator from specified data source.
 * IOperator is a abstract handler for input data, more then one operator can be combined.
 */
public class Looper {

    public static <T> IOperator<T> from(final T item) {
        ArgumentChecker.required(item, "item");
        return new OrderedSource<>(item);
    }

    /**
     * Construct IOperator from data array.
     *
     * @param   items
     *          The input data array
     * @param   <T>
     *          The input data type
     * @return  The operator which can emit data and combine other operator
     */
    public static <T> IOperator<T> from(final T... items) {
        ArgumentChecker.required(items, "items");
        return new OrderedSource<>(items);
    }

    /**
     * Construct IOperator from data collection.
     *
     * @param   items
     *          The input data collection
     * @param   <T>
     *          The input data type
     * @return  The operator which can emit data and combine other operator
     */
    public static <T> IOperator<T> from(Collection<T> items) {
        ArgumentChecker.required(items, "items");
        return new CollectionSource<>(items);
    }

    public static <T> IOperator<T> from(Iterator<T> iterator) {
        ArgumentChecker.required(iterator, "iterator");
        return new IteratorSource<>(iterator);
    }

    public static <T> IOperator<T> from(Iterable<T> iterable) {
        return from(iterable.iterator());
    }
}
