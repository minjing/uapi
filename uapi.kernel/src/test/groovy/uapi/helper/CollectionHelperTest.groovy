package uapi.helper

import spock.lang.Specification

/**
 * Test case for CollectionHelper
 */
class CollectionHelperTest extends Specification {

    def 'Test isContains method on collection'() {
        expect:
        CollectionHelper.isContains(collection, item1, item2) == isContains

        where:
        collection      | item1                 | item2     | isContains
        ["1", "2"]      | new String("1")       | "3"       | true
        ["1", "2"]      | "3"                   | "4"       | false
    }

    def 'Test isContains method on array'() {
        expect:
        CollectionHelper.isContains(array, item1, item2) == isContains

        where:
        array                   | item1                 | item2     | isContains
        ["1", "2"] as String[]  | new String("1")       | "3"       | true
        ["1", "2"] as String[]  | "3"                   | "4"       | false
    }

    def 'Test isStrictContains method on collection'() {
        expect:
        CollectionHelper.isStrictContains(collection, item1, item2) == result

        where:
        collection      | item1                 | item2                 | result
        ["1", "2"]      | new String("1")       | new String("2")       | false
        ["1", "2"]      | "1"                   | "2"                   | true
    }

    def 'Test isStrictContains method on array'() {
        expect:
        CollectionHelper.isStrictContains(array, item1, item2) == result

        where:
        array                   | item1                 | item2                 | result
        ["1", "2"] as String[]  | new String("1")       | new String("2")       | false
        ["1", "2"] as String[]  | "1"                   | "2"                   | true
    }

    def 'Test contains method on collection'() {
        expect:
        CollectionHelper.contains(collection, item1, item2) == result

        where:
        collection      | item1                 | item2     | result
        ["1", "2"]      | new String("1")       | "3"       | "1"
        ["1", "2"]      | "3"                   | "4"       | null
    }

    def 'Test contains method on array'() {
        expect:
        CollectionHelper.contains(array, item1, item2) == result

        where:
        array                   | item1                 | item2     | result
        ["1", "2"] as String[]  | new String("1")       | "3"       | "1"
        ["1", "2"] as String[]  | "3"                   | "4"       | null
    }

    def 'Test strictContains method on collection'() {
        expect:
        CollectionHelper.strictContains(collection, item1, item2) == result

        where:
        collection      | item1                 | item2                 | result
        ["1", "2"]      | new String("1")       | new String("2")       | null
        ["1", "2"]      | "1"                   | "2"                   | "1"
    }

    def 'Test strictContains method on array'() {
        expect:
        CollectionHelper.strictContains(array, item1, item2) == result

        where:
        array                   | item1                 | item2                 | result
        ["1", "2"] as String[]  | new String("1")       | new String("2")       | null
        ["1", "2"] as String[]  | "1"                   | "2"                   | "1"
    }
}
