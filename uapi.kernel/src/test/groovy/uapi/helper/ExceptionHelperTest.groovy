package uapi.helper

import spock.lang.Specification

/**
 * Unit test for ExceptionHelper
 */
class ExceptionHelperTest extends Specification {

    def 'test getStackString method'() {
        given:
        def t = new Throwable()

        expect:
        String str = ExceptionHelper.getStackString(t)
        StringHelper.firstLine(str) == 'java.lang.Throwable'
    }
}
