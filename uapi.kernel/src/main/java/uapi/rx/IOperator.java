/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

import uapi.helper.Functionals;

import java.util.List;
import java.util.Map;

/**
 * All rx operator need to implement this interface
 */
public interface IOperator<T> {

    /**
     * Return a operator which can map input data to output data by specific logic
     *
     * @param   operator
     *          The logic used for map input data to output data
     * @param   <O>
     *          The output data type
     * @return  The map operator
     */
    <O> IOperator<O> map(Functionals.Convert<T, O> operator);

    /**
     * Return a operator which can filter out input data by specific logic
     *
     * @param   operator
     *          The filter logic
     * @return  The filter operator instance
     */
    IOperator<T> filter(Functionals.Filter<T> operator);

    /**
     * Construct an operator which can limit data size by specified count
     *
     * @param   count
     *          Limited count
     * @return  The limitation operator
     */
    IOperator<T> limit(int count);

    /**
     * Iterate all of input data by specific logic
     * @param   action
     *          The iteration logic
     */
    void foreach(Functionals.Action<T> action);

    /**
     * Iterate all of input data with its index by specific logic
     *
     * @param   action
     *          The iteration logic
     */
    void foreachWithIndex(IndexedForeachOperator.IndexedForeach<T> action);

    /**
     * Return first element of data
     *
     * @return  The first element
     */
    T first();

    /**
     * Return all element to a list
     *
     * @return  A list contains all element
     */
    List<T> toList();

    /**
     * Return all element to a map
     *
     * @param   <KT>
     *          The type of key of the map
     * @param   <VT>
     *          The type of value of the value
     * @return  A map contains all element
     */
    <KT, VT> Map<KT, VT> toMap();
}
