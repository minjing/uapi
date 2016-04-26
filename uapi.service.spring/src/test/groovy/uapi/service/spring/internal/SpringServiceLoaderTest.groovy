package uapi.service.spring.internal

import spock.lang.Specification
import uapi.service.IRegistry

/**
 * Unit test for SpringServiceLoader
 */
class SpringServiceLoaderTest extends Specification {

    def 'Test init method'() {
        given:
        def registry = Mock(IRegistry)
        def loader = new SpringServiceLoader()
        loader._cfgFile = 'appctx.xml'
        loader._registry = registry
        loader.init()

        expect:
        TestBean bean = loader.load(svcId)
        bean != null
        bean.name == name


        where:
        svcId       | name
        'testBean'  | 'MyName'
    }
}
