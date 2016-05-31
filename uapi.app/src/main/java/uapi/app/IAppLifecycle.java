/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app;

/**
 * The IAppLifecycle contains some callback methods which will be invoked by application lifecycle
 */
public interface IAppLifecycle {

    /**
     * Invoked when application is started
     */
    void onStarted();

    /**
     * Invoked when application is stopped
     */
    void onStopped();
}
