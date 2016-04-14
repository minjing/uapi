package uapi.helper

import spock.lang.Specification
import uapi.InvalidArgumentException

/**
 * Test case for {@code ArgumentChecker}
 */
class ArgumentCheckerTest extends Specification {

    def 'Test check int'() {
        when:
        ArgumentChecker.checkInt(arg, argName, min, max)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg                     | argName   | min               | max               | exception
        -1                      | "test"    | 0                 | 10                | InvalidArgumentException
        11                      | "test"    | 0                 | 10                | InvalidArgumentException
        -3                      | "test"    | -2                | -1                | InvalidArgumentException
    }

    def 'Test notNull method'() {
        when:
        ArgumentChecker.notNull(arg, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg     | argName   | exception
        null    | "test"    | InvalidArgumentException
    }

    def 'Test notEmpty method'() {
        when:
        ArgumentChecker.notEmpty(arg, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg     | argName   | exception
        null    | "test"    | InvalidArgumentException
        ""      | "test"    | InvalidArgumentException
        " "     | "test"    | InvalidArgumentException
        "\t"    | "test"    | InvalidArgumentException
        "\r\n"  | "test"    | InvalidArgumentException
    }

    def 'test notEmpty for array'() {
        when:
        ArgumentChecker.notEmpty(arg, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg             | argName   | exception
        [] as String[]  | "test"    | InvalidArgumentException
    }
}
