package uapi.service.internal

import spock.lang.Specification
import uapi.service.IRegistry
import uapi.service.IService
import uapi.service.IWatcher
import uapi.service.Injection

/**
 * Test case for Registry
 */
class RegistryTest extends Specification {

    Registry registry;

    def setup() {
        registry = new Registry()
    }

    def "Test get id"() {
        expect:
        registry.getIds() == [IRegistry.canonicalName] as String[]
    }

    def "Register a normal service with id"() {
        when:
        registry.register(service, serviceId)

        then:
        registry.findService(serviceId) == service

        where:
        serviceId   | service
        "1"         | Mock(Object)
    }

    def "Register a IService instance with id"() {
        def svc = Mock(IService) {
            getIds() >> ["1", "2"]
        }

        when:
        registry.register(svc)

        then:
        registry.findService("1") == svc
        registry.findService("2") == svc
        registry.getCount() == 2
    }

    def "Register more IService instances"() {
        def svc1 = Mock(IService) {
            getIds() >> ["1", "2"]
        }
        def svc2 = Mock(IService) {
            getIds() >> ["3", "4"]
        }

        when:
        registry.register(svc1, svc2)

        then:
        registry.findService("1") == svc1
        registry.findService("2") == svc1
        registry.findService("3") == svc2
        registry.findService("4") == svc2
        registry.getCount() == 4
//        registry.getResolvedCount() == 4
//        registry.getUnresolvedCount() == 0
    }

//    def "Register service with watcher"() {
//        def watcher = Mock(IWatcher)
//        def svc = Mock(IService) {
//            getIds() >> "1"
//        }
//
//        when:
//        registry.injectObject(new Injection(IWatcher.class.name, watcher))
//        registry.register(svc)
//
//        then:
//        1 * watcher.onRegister(_)
//        1 * watcher.onResolved(_)
//    }
}
