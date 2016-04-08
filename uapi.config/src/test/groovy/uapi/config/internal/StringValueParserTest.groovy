package uapi.config.internal

import spock.lang.Specification

/**
 * Created by xquan on 4/7/2016.
 */
class StringValueParserTest extends Specification {

    def 'Test supported types'() {
        given:
        StringValueParser parser = new StringValueParser()

        expect:
        parser.isSupport(inType, outType)

        where:
        inType                  | outType
        String.canonicalName    | String.canonicalName
    }

    def 'Test parse'() {
        given:
        StringValueParser parser = new StringValueParser()

        expect:
        parser.parse(input) == output

        where:
        input   | output
        'abc'   | 'abc'
    }
}
