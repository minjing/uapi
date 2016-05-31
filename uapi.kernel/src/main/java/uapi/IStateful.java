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
 * Represent an object with specific state
 * 
 * @author min
 */
public interface IStateful {

    /**
     * Initial state
     */
    int STATE_INIT      = 0;

    /**
     * Terminal state
     */
    int STATE_TERMINAL  = 128;

    /**
     * Set the state change watcher
     * 
     * @param   watcher
     *          The state change watcher
     */
    void setWatcher(IStateWatcher watcher);

    /**
     * Get the state change watcher
     * 
     * @return  The state change watcher
     */
    IStateWatcher getWatcher();

    /**
     * Return current state
     * 
     * @return  Current state
     */
    int getState();
}
