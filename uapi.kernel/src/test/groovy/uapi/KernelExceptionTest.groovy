package uapi

import spock.lang.Specification

/**
 * Test case for KernelException
 */
class KernelExceptionTest extends Specification {

    def 'Test message construction'() {
        given:
        KernelException ex = new KernelException(template, arg1, arg2)

        expect:
        ex.getMessage() == result

        where:
        template        | arg1  | arg2  | result
        "tet {} tt {}"  | "aa"  | "bb"  | "tet aa tt bb"
    }
}
