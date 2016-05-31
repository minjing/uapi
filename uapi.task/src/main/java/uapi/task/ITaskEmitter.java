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
 * The interface will be used emit task to framework
 * 
 * @author min
 */
public interface ITaskEmitter {

    /**
     * Emit task to framework
     * 
     * @param   task
     *          The task which will be add to framework
     */
    void emit(ITask task);

    /**
     * Emit task with notifier to framework
     * 
     * @param   task
     *          The task which will be emitted
     * @param   notifier
     *          The notifier which will be invoked when specified event happened
     */
    void emit(ITask task, INotifier notifier);
}
