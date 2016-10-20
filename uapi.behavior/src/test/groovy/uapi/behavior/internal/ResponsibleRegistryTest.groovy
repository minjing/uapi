package uapi.behavior.internal

import spock.lang.Specification
import uapi.behavior.IEventDrivenBehavior
import uapi.behavior.IResponsible
import uapi.event.IEventBus
import uapi.log.ILogger

/**
 * Unit test for ResponsibleRegistry
 */
class ResponsibleRegistryTest extends Specification {

    def 'Test init'() {
        given:
        ResponsibleRegistry reg = new ResponsibleRegistry()
        def eventBus = Mock(IEventBus)
        reg._eventBus = eventBus
        reg._logger = Mock(ILogger)
        reg._responsibles.add(Mock(IResponsible) {
            behaviors() >> Mock(IEventDrivenBehavior) {
                topic() >> 'event-topic'
            }
        })

        when:
        reg.init()

        then:
        noExceptionThrown()
        1 * eventBus.register(_)
    }
}
