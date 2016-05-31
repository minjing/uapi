/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

/**
 * A resolver can encode a value for specific type to another specific type or reverse.
 * Encode means: convert IT to OT
 * Decode means: convert OT to IT
 */
public interface IValueResolver<IT, OT> {

    /**
     * Encode value from type IT to OT
     *
     * @param   value
     *          The instance of IT
     * @return  The instance of OT
     */
    OT encode(IT value);

    /**
     * Decode value from type OT to IT
     *
     * @param   value
     *          The instance of OT
     * @return  The instance of IT
     */
    IT decode(OT value);
}
