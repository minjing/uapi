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

/**
 * Created by min on 16/6/12.
 */
public interface IOperator<T> {

    <O> IOperator<O> map(Functionals.Convert<T, O> operator);

    IOperator<T> filter(Functionals.Filter<T> operator);

    void foreach(Functionals.Action<T> action);
}
