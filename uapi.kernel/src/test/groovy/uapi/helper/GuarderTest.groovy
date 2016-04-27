package uapi.helper

import spock.lang.Specification

import java.util.concurrent.locks.Lock

/**
 * Unit test for Guarder
 */
class GuarderTest extends Specification {

    def 'Test run'() {
        given:
        Lock lock = Mock(Lock)
        final List<String> strs = new ArrayList<>()

        when:
        Guarder.by(lock).run({ strs.add("Hello"); })

        then:
        strs.size() == 1
        strs.get(0) == "Hello"
        1 * lock.lock()
        1 * lock.unlock()
    }

    def 'Test run by result'() {
        given:
        Lock lock = Mock(Lock)

        when:
        String result = Guarder.by(lock).runForResult({ return "Hello"; })

        then:
        result == "Hello"
        1 * lock.lock()
        1 * lock.unlock()
    }
}
