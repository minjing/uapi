package uapi.service.internal

import spock.lang.Specification
import uapi.service.IRegistry
import uapi.service.ISatisfyHook
import uapi.service.IService
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
        serviceId | service
        "1"       | Mock(Object)
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
    }

    def "Test Optional"() {
        expect:
        registry.isOptional(svcId) == optional

        where:
        svcId                       | optional
        ISatisfyHook.canonicalName  | true
    }

    def "Test Satisfy Invocation"() {
        def svc1 = Mock(IService) {
            getIds() >> ["1", "2"]
        }
        def svc2 = Mock(IService) {
            getIds() >> ["3", "4"]
        }

        given:
        ISatisfyHook hook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true
        }
        Injection injection = Mock(Injection) {
            getId() >> ISatisfyHook.canonicalName
            getObject() >> hook
        }
        registry.injectObject(injection)

        when:
        registry.register(svc1, svc2)

        then:
        registry.findService("1") == svc1
        registry.findService("2") == svc1
        registry.findService("3") == svc2
        registry.findService("4") == svc2
        registry.getCount() == 4
    }

    def 'Test find service by id and from'() {
        def svc1 = Mock(IService) {
            getIds() >> ["1", "2"]
        }
        def svc2 = Mock(IService) {
            getIds() >> ["3", "4"]
        }

        given:
        ISatisfyHook hook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true
        }
        Injection injection = Mock(Injection) {
            getId() >> ISatisfyHook.canonicalName
            getObject() >> hook
        }
        registry.injectObject(injection)

        when:
        registry.register(svc1, svc2)

        then:
        registry.findService("1", IRegistry.FROM_LOCAL) == svc1
        registry.findService("2", IRegistry.FROM_LOCAL) == svc1
        registry.findService("3", IRegistry.FROM_LOCAL) == svc2
        registry.findService("4", IRegistry.FROM_LOCAL) == svc2
        registry.getCount() == 4
    }
}
