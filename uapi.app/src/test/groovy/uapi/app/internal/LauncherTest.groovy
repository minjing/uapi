package uapi.app.internal

import spock.lang.Specification
import uapi.app.IAppLifecycle
import uapi.log.ILogger
import uapi.service.ILifecycle

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
        launcher._logger = logger;
        launcher._lifecycles.add(appLife)

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
        1 * appLife.onStarted()
        1 * appLife.onStopped()
    }
}
