package uapi.config.internal

import spock.lang.Specification
import uapi.config.IConfigFileParser
import uapi.config.IConfigTracer
import uapi.log.ILogger

/**
 * Test case for FileBasedConfigProvider
 */
class FileBasedConfigProviderTest extends Specification {

    def 'Test config'() {
        def cfgTracer = Mock(IConfigTracer)
        def yamlParser = Mock(IConfigFileParser) {
            isSupport('yml') >> true
            parse(_) >> ['key': 'value']
        }

        given:
        FileBasedConfigProvider provider = new FileBasedConfigProvider()
        provider._logger = Mock(ILogger)
        provider._cfgTracer = cfgTracer
        provider._parsers.add(yamlParser)

        when:
        provider.config(FileBasedConfigProvider.CFG_FILE_PATH, 'src/test/resources/config.yml')

        then:
        1 * cfgTracer.onChange(['key': 'value'])
    }
}
