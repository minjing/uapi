/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.web;

import uapi.IIdentifiable;
import uapi.KernelException;

/**
 * Created by xquan on 5/25/2016.
 */
public interface IStringCodec<T> extends IIdentifiable<String> {

    String decode(T value, Class<T> type) throws KernelException;

    T encode(String value, Class<T> type) throws KernelException;
}
