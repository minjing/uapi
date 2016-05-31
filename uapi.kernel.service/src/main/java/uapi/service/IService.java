/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.annotation.Type;

/**
 * Implement this interface will indicate the object is managed by framework
 */
public interface IService {

    String METHOD_GETIDS                        = "getIds";
    String METHOD_GET_DEPENDENT_ID              = "getDependentIds";
    String METHOD_GETIDS_RETURN_TYPE            = Type.STRING_ARRAY;
    String METHOD_GET_DEPENDENT_ID_RETURN_TYPE  = Type.STRING_ARRAY;

    /**
     * Return the service identifications
     *
     * @return  The service identifications
     */
    String[] getIds();
}
