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
 * The task manager is entry point of whole task framework
 * It can add task directly or register new task producer outside
 * 
 * @author min
 */
public interface ITaskManager {

    /**
     * Add new task
     * 
     * @param task  The task which will be added
     */
    void addTask(ITask task);

    /**
     * Add new task and a notifier which will be used when task is done or failed
     * 
     * @param task      The new task which will be added
     * @param notifier  The associated notified which will be used when task is done or failed
     */
    void addTask(ITask task, INotifier notifier);
    
    /**
     * Register a new task producer which will generate new task
     * 
     * @param producer  The task producer which will generate new task
     */
    void registerProducer(ITaskProducer producer);
}
