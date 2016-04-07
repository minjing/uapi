package uapi.config.internal

import spock.lang.Specification

/**
 * Test case for LongValueParser
 */
class LongValueParserTest extends Specification {

    def 'Test supported types'() {
        given:
        LongValueParser parser = new LongValueParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                  | outType
        Long.canonicalName      | Long.canonicalName
        String.canonicalName    | Long.canonicalName
        Long.canonicalName      | 'long'
        String.canonicalName    | 'long'
    }

    def 'Test parse function'() {
        given:
        LongValueParser parser = new LongValueParser()

        expect:
        parser.parse(input) == output

        where:
        input           | output
        23L             | 23L
        '23'            | 23L
        new Long(23L)   | 23L
        '23'            | 23L
    }
}
