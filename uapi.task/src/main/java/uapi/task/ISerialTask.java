/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.task;

/**
 * The tasks only can be executed one by one by its serial number
 * 
 * @author min
 */
public interface ISerialTask {

    /**
     * Get the serial id for this task
     * 
     * @return  The associated serial number of this task
     */
    String getSerialId();
}
