package uapi.config.internal

import spock.lang.Specification

/**
 * Test case for FloatValueParser
 */
class FloatValueParserTest extends Specification {

    def 'Test support types'() {
        given:
        FloatValueParser parser = new FloatValueParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                  | outType
        Float.canonicalName     | Float.canonicalName
        String.canonicalName    | Float.canonicalName
        String.canonicalName    | 'float'
    }

    def 'Test parse value'() {
        given:
        FloatValueParser parser = new FloatValueParser()

        expect:
        parser.parse(input) == output

        where:
        input       | output
        34.2f       | new Float(34.2f)
        "34.2"      | 34.2f
    }
}
