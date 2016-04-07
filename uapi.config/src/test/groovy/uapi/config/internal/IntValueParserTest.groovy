package uapi.config.internal

import spock.lang.Specification

/**
 * Created by xquan on 4/7/2016.
 */
class IntValueParserTest extends Specification {

    def 'Test parser supported types'() {
        given:
        IntValueParser parser = new IntValueParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                  | outType
        String.canonicalName    | Integer.canonicalName
        String.canonicalName    | 'int'
        Integer.canonicalName   | Integer.canonicalName
        Integer.canonicalName   | 'int'
    }

    def 'Test parse string to int'() {
        given:
        IntValueParser parser = new IntValueParser()

        expect:
        parser.parse(input) == output

        where:
        input   | output
        '12'    | 12
        12      | 12
    }
}
