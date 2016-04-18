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

    def 'test equals method'() {
        expect:
        ArgumentChecker.equals(arg, expected, argName)

        where:
        arg     | argName   | expected
        "a"     | "Test"    | "a"
        null    | "Test"    | null
    }

    def 'test not equals method'() {
        when:
        ArgumentChecker.notEquals(arg, expected, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg     | argName   | expected  | exception
        "a"     | "Test"    | "a"       | InvalidArgumentException
    }

    def 'test not contains string'() {
        when:
        ArgumentChecker.notContains(arg, unexpected, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg     | argName   | unexpected    | exception
        "/ab"   | "Test"    | "/"           | InvalidArgumentException
        "\\bb"  | "Test"    | "\\"          | InvalidArgumentException
        ""      | "Test"    | ""            | InvalidArgumentException
        "ab cd" | "Test"    | " "           | InvalidArgumentException
    }

    def 'test not contains on collection'() {
        when:
        ArgumentChecker.notContains(arg, argName, unexpected)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg         | argName   | unexpected    | exception
        ["a", "b"]  | "Test"    | "a"           | InvalidArgumentException
        ["a", "b"]  | "Test"    | "b"           | InvalidArgumentException
    }

    def 'test not contains on collection2'() {
        when:
        ArgumentChecker.notContains(arg, argName, unexpected1, unexpected2)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg         | argName   | unexpected1   | unexpected2   | exception
        ["a", "b"]  | "Test"    | "a"           | "c"           | InvalidArgumentException
        ["a", "b"]  | "Test"    | "b"           | "d"           | InvalidArgumentException
    }

    def 'test required'() {
        when:
        ArgumentChecker.required(arg, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg     | argName   | exception
        null    | "Test"    | InvalidArgumentException
        ""      | "Test"    | InvalidArgumentException
        "   "   | "Test"    | InvalidArgumentException
        "\t"    | "Test"    | InvalidArgumentException
        "\r\n"  | "Test"    | InvalidArgumentException
    }

    def 'test not zero method'() {
        when:
        ArgumentChecker.notZero(arg, argName)

        then:
        def e = thrown(exception)
        e.class == exception

        where:
        arg     | argName   | exception
        null    | "Test"    | InvalidArgumentException
        []      | "Test"    | InvalidArgumentException
    }
}
