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
 * A task is an abstract executable unit
 * 
 * @author min
 */
public interface ITask {

    /**
     * The task entry point
     */
    void run();

    /**
     * Return the priority of this task
     * The priority value must be between 0 to 128
     * 
     * @return  The priority
     */
    int getPriority();

    /**
     * Return the description of the task
     * 
     * @return  Task description
     */
    String getDescription();
}
