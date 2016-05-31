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

/**
 * The String resolve
 */
public interface IStringResolver<T> extends IIdentifiable<String> {

    /**
     * Encode value from type IT to OT
     *
     * @param   value
     *          The instance of IT
     * @return  The instance of OT
     */
    String encode(T value, String formatterName);

    /**
     * Decode value from type OT to IT
     *
     * @param   value
     *          The instance of OT
     * @return  The instance of IT
     */
    T decode(String value, String formatterName);
}
