/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi;

/**
 * The watcher watch object state change event
 * 
 * @author min
 */
public interface IStateWatcher {

    /**
     * Invoked when the object state was changed
     * 
     * @param   which
     *          The object which state was changed
     * @param   oldState
     *          The old state
     * @param   newState
     *          The new state
     */
    void stateChanged(IStateful which, int oldState, int newState);

    /**
     * Invoked when the object state was changed
     * 
     * @param   which
     *          The object which state was changed
     * @param   oldState
     *          The old state
     * @param   newState
     *          The new state
     * @param   t
     *          raised exception during state change
     */
    void stateChange(IStateful which, int oldState, int newState, Throwable t);
}
