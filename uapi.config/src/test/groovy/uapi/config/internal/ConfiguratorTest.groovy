package uapi.config.internal

import spock.lang.Specification
import uapi.config.Configuration
import uapi.config.IConfigurable

/**
 * Test case for Configurator
 */
class ConfiguratorTest extends Specification {

    def 'Test is satisfied at first time'() {
        def svc = Mock(IConfigurable) {
            getPaths() >> ['test']
        }
        def cfg = Mock(Configuration) {
            bindConfigurable('test', svc) >> true
        }

        given:
        Configurator configurator = new Configurator()

        expect:
        ! configurator.isSatisfied(svc)
    }

    def 'Test is satisfied at second time'() {
        def svc = Mock(IConfigurable) {
            getPaths() >> ['test']
        }

        given:
        Configurator configurator = new Configurator()

        when:
        configurator.onChange('test', 'value')

        then:
        configurator.isSatisfied(svc)
    }
}
