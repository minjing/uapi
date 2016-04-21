package uapi.service.internal

import spock.lang.Specification
import uapi.service.IInitial
import uapi.service.IInjectable
import uapi.service.IRegistry
import uapi.service.ISatisfyHook
import uapi.service.IService
import uapi.service.IServiceFactory

/**
 * Test case for ServiceHolder
 */
class ServiceHolderTest extends Specification {

    def "Test add a service"() {
        given:
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(from, service, serviceId, mockHook)
        mockHook.isSatisfied(service) >> true

        expect:
        holder.id == serviceId
        holder.service == service
//        holder.isSatisfied() == resolved
        holder.inited == inited

        where:
        serviceId   | from                  | service           | resolved  | inited
        "1"         | IRegistry.FROM_LOCAL  | Mock(Object)      | true      | false
        "2"         | IRegistry.FROM_LOCAL  | Mock(IService)    | true      | false
    }

    def "Test init service on a normal service"() {
        given:
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(from, service, serviceId, mockHook)
        mockHook.isSatisfied(service) >> true

        when:
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.service == service
//        holder.isSatisfied() == resolved
        holder.inited == inited

        where:
        serviceId   | from                  | service           | resolved  | inited
        "1"         | IRegistry.FROM_LOCAL  | Mock(Object)      | true      | true
        "2"         | IRegistry.FROM_LOCAL  | Mock(IService)    | true      | true
    }

        def "Test init service which is IInitial instance"() {
        given:
        IInitial initialSvc = Mock(IInitial)
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(from, initialSvc, serviceId, mockHook)
        mockHook.isSatisfied(initialSvc) >> true

        when:
        holder.id == serviceId
        holder.service == initialSvc
//        holder.isSatisfied() == resolved
        holder.tryInitService()

        then:
        1 * initialSvc.init()

        where:
        serviceId   | from                  | resolved  | inited
        "3"         | IRegistry.FROM_LOCAL  | true      | true
    }

    def "Test service with dependency"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(from, injectableSvc, serviceId, ["dep01@Local", "dep02@Local"] as String[], mockHook)
        mockHook.isSatisfied(injectableSvc) >> true

        when:
        holder.isDependsOn("dep01@Local")

        then:
        holder.id == serviceId

        where:
        serviceId   | from                  | resolved  | inited    | dependId
        "1"         | IRegistry.FROM_LOCAL  | false     | false     | "dep01"
    }
/*
    def "Test set service dependency"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        ServiceHolder dependSvc = Mock(ServiceHolder) {
            getId() >> 'dep01@Local'
            isSatisfied() >> true
            getService() >> new Object()
        }
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(injectableSvc) >> true
        }
        ServiceHolder holder = new ServiceHolder(from, injectableSvc, serviceId, ["dep01@Local"] as String[], mockHook)

        when:
        holder.setDependency(dependSvc)
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.satisfied == resolved
        holder.inited == inited
        1 * injectableSvc.injectObject(_)

        where:
        serviceId   | from                  | resolved  | inited
        "1"         | IRegistry.FROM_LOCAL  | true      | true
    }

    def "Test depends on a service factory"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        IServiceFactory svcFactory = Mock(IServiceFactory) {
            createService(injectableSvc) >> new Object()
        }
        ServiceHolder dependSvc = Mock(ServiceHolder) {
            getId() >> 'dep01@Local'
            isSatisfied() >> true
            getService() >> svcFactory
        }
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(injectableSvc) >> true
        }
        ServiceHolder holder = new ServiceHolder(from, injectableSvc, serviceId, ["dep01@Local"] as String[], mockHook)

        when:
        holder.setDependency(dependSvc)
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.satisfied == resolved
        holder.inited == inited
        1 * injectableSvc.injectObject(_)

        where:
        serviceId   | from                  | resolved  | inited
        "1"         | IRegistry.FROM_LOCAL  | true      | true
    }
    */
}
