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
 * A notifier is used for task status notifying when the task
 * is done or failed by framework
 * 
 * @author min
 */
public interface INotifier {

    /**
     * Invoked when the task is done
     * 
     * @param task  The task which is done by framework
     */
    void onDone(ITask task);

    /**
     * Invoked when processing the task failed
     * 
     * @param task  The task which is processed failed by framework
     * @param t     The exception object if it has
     */
    void onFailed(ITask task, Throwable t);
}
