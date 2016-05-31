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
 * A task producer used to generate new task
 * 
 * @author min
 */
public interface ITaskProducer extends Runnable {

    /**
     * Inject task emitter which used to emit task to framework
     * 
     * @param taskEmitter   The task emitter which used to emit task to framework
     */
    void setEmitter(ITaskEmitter taskEmitter);
}
