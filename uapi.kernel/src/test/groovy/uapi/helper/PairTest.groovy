package uapi.helper

import spock.lang.Specification
import uapi.InvalidArgumentException

/**
 * Unit test for Pair
 */
class PairTest extends Specification {

    def 'test split to'() {
        when:
        Pair pair = Pair.splitTo(str, sep);

        then:
        pair.leftValue == lv
        pair.rightValue == rv

        where:
        str     | sep   | lv                    | rv
        "a@B"   | "@"   | "a"                   | "B"
        "a@"    | "@"   | "a"                   | StringHelper.EMPTY
        "@b"    | "@"   | StringHelper.EMPTY    | "b"
    }

    def 'test split to with error'() {
        when:
        Pair.splitTo(str, sep);

        then:
        thrown(InvalidArgumentException)

        where:
        str     | sep
        null    | "@"
        "a"     | null
    }

    def 'test equals'() {
        given:
        Pair p1 = Pair.splitTo(str1, sep)
        Pair p2 = Pair.splitTo(str2, sep)

        expect:
        p1 == p2

        where:
        str1    | str2      | sep
        "a@b"   | "a@b"     | "@"
    }
}
