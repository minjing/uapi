/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal

import spock.lang.Ignore
import spock.lang.Specification
import uapi.app.IAppLifecycle
import uapi.log.ILogger
import uapi.service.IRegistry

/**
 * Test case for Launch
 */
class ApplicationTest extends Specification {

    def Application app

    def 'Test launch'() {
        given:
        ILogger logger = Mock(ILogger)
        IAppLifecycle appLife = Mock(IAppLifecycle) {
            getAppName() >> 'Test'
        }
        IRegistry registry = Mock(IRegistry)
        app = new Application()
        app._appName = 'Test'
        app._logger = logger
        app._lifecycles.add(appLife)
        app._registry = registry

        when:
        new Thread(new Runnable() {
            @Override
            void run() {
                app.startup(System.currentTimeMillis())
            }
        }).start()
        while(true) {
            if (app.state() == Application.AppState.STARTED) {
                break;
            }
            Thread.sleep(100)
        }
        app.stop()
        while (true) {
            if (app.state() == Application.AppState.STOPPED) {
                break;
            }
        }

        then:
        1 * appLife.onStarted()
        1 * appLife.onStopped()
    }
}
