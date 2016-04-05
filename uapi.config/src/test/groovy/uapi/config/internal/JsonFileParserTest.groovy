package uapi.config.internal

import spock.lang.Specification
import uapi.log.ILogger

/**
 * Test case for JsonFileParser
 */
class JsonFileParserTest extends Specification {

    def 'Test supported file extensions'() {
        given:
        JsonFileParser parser = new JsonFileParser()

        expect:
        parser.isSupport(extension) == supported

        where:
        extension       | supported
        'yml'           | false
        'yaml'          | false
        'xml'           | false
        'json'          | true
        'properties'    | false
    }

    def 'Test parse yaml file'() {
        given:
        File yamlFile = new File('src/test/resources/config.json')
        JsonFileParser parser = new JsonFileParser()
        parser._logger = Mock(ILogger)

        expect:
        Map config = parser.parse(yamlFile)
        config != null
        config.name == 'My Name'
        config.address[0].home == 'Home address'
        config.address[1].office == 'Office address'
    }
}
