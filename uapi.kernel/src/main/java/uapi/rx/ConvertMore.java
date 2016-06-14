/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

/**
 * Created by xquan on 6/14/2016.
 */
@FunctionalInterface
public interface ConvertMore<I, O> {
    IOperator<O> accept(I in);
}
