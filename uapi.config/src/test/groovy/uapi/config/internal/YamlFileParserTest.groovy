package uapi.config.internal

import spock.lang.Specification
import uapi.log.ILogger

/**
 * Test case for YamFileParser
 */
class YamlFileParserTest extends Specification {

    def 'Test supported file extensions'() {
        given:
        YamlFileParser parser = new YamlFileParser()

        expect:
        parser.isSupport(extension) == supported

        where:
        extension       | supported
        'yml'           | true
        'yaml'          | true
        'xml'           | false
        'json'          | false
        'properties'    | false
    }

    def 'Test parse yaml file'() {
        given:
        File yamlFile = new File('src/test/resources/config.yml')
        YamlFileParser parser = new YamlFileParser()
        parser._logger = Mock(ILogger)

        expect:
        Map config = parser.parse(yamlFile)
        config != null
        config.name == 'My Name'
        config.address[0].home == 'Home address'
        config.address[1].office == 'Office address'
    }
}
