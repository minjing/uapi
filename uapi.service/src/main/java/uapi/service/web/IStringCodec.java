/*
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
 * Encode a type to string and decode a type to string
 */
public interface IStringCodec<T> extends IIdentifiable<String> {

    /**
     * Decode specific type instance to string
     *
     * @param   value
     *          The specific type instance
     * @param   type
     *          The specific type
     * @return  The encoded string
     * @throws  KernelException
     */
    String decode(T value, Class<T> type) throws KernelException;

    /**
     * Encode string to a specific type instance
     *
     * @param   value
     *          The string need to encode
     * @param   type
     *          The specific type
     * @return  The encoded type instance
     * @throws  KernelException
     */
    T encode(String value, Class<T> type) throws KernelException;
}
