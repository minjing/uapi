/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal

import spock.lang.Specification
import uapi.app.IAppLifecycle
import uapi.log.ILogger
import uapi.service.IRegistry

/**
 * Test case for Launch
 */
class LauncherTest extends Specification {

    def Launcher launcher

    def 'Test launch'() {
        given:
        launcher = new Launcher()
        ILogger logger = Mock(ILogger)
        IAppLifecycle appLife = Mock(IAppLifecycle)
        IRegistry registry = Mock(IRegistry)
        launcher._logger = logger;
        launcher._lifecycles.add(appLife)
        launcher._registry = registry

        when:
        new Thread(new Runnable() {
            @Override
            void run() {
                launcher.launch(System.currentTimeMillis())
            }
        }).start()
        Thread.sleep(100)
        launcher.stop()
        Thread.sleep(100)

        then:
        1 * registry.loadExternalServices(_)
        1 * appLife.onStarted()
        1 * appLife.onStopped()
    }
}
