package uapi.config.internal

import spock.lang.Specification

/**
 * Test case for DoubleValueParser
 */
class DoubleValueParserTest extends Specification {

    def 'Test supported types'() {
        given:
        DoubleValueParser parser = new DoubleValueParser()

        expect:
        parser.isSupport(intype, outtype)

        where:
        intype                  | outtype
        Double.canonicalName    | Double.canonicalName
        String.canonicalName    | Double.canonicalName
        String.canonicalName    | 'double'
        Double.canonicalName    | 'double'
    }

    def 'Test parse value'() {
        given:
        DoubleValueParser parser = new DoubleValueParser()

        expect:
        parser.parse(input) == output

        where:
        input               | output
        '12.3'              | 12.3d
        new Double(12.3d)   | 12.3d
        '12.3'              | new Double(12.3d)
    }
}
