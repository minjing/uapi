package uapi.config.internal

import spock.lang.Specification
import uapi.config.Configuration
import uapi.config.IConfigurable
import uapi.service.IServiceReference

/**
 * Test case for Configurator
 */
class ConfiguratorTest extends Specification {

    def 'Test is satisfied at first time'() {
        def cfguable = Mock(IConfigurable) {
            getPaths() >> ['test']
        }
        def svc = Mock(IServiceReference) {
            getService() >> cfguable
        }

        given:
        Configurator configurator = new Configurator()

        expect:
        ! configurator.isSatisfied(svc)
    }

    def 'Test is satisfied at second time'() {
        def configurable = Mock(IConfigurable) {
            getPaths() >> ['test']
        }
        def svc = Mock(IServiceReference) {
            getService() >> configurable
        }

        given:
        Configurator configurator = new Configurator()

        when:
        configurator.onChange('test', 'value')

        then:
        configurator.isSatisfied(svc)
    }
}
