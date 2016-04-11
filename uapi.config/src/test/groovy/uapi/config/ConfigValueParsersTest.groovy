package uapi.config

import spock.lang.Specification

/**
 * Test case for ConfigValueParsers
 */
class ConfigValueParsersTest extends Specification {

    def 'Test find parser by type'() {
        def IConfigValueParser mockParser = Mock(IConfigValueParser)
        mockParser.isSupport(inType, outType) >> {
            return true;
        }

        given:
        ConfigValueParsers parsers = new ConfigValueParsers()
        parsers._parsers.add(mockParser)

        expect:
        parsers.findParser(inType, outType) == mockParser

        where:
        inType                  | outType
        String.canonicalName    | Integer.canonicalName
        'int'                   | Long.canonicalName
    }

    def 'Test find parser by name'() {
        def IConfigValueParser mockParser = Mock(IConfigValueParser)
        mockParser.getName() >> {
            return parserName;
        }

        given:
        ConfigValueParsers parsers = new ConfigValueParsers()
        parsers._parsers.add(mockParser)

        expect:
        parsers.findParser(parserName) == mockParser

        where:
        parserName  | node
        'IntParser' | ''
    }
}
