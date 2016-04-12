package uapi.service.internal

import spock.lang.Specification
import uapi.service.IInitial
import uapi.service.IInjectable
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
        ServiceHolder holder = new ServiceHolder(service, serviceId, mockHook)
        mockHook.isSatisfied(service) >> true

        expect:
        holder.id == serviceId
        holder.service == service
        holder.isSatisfied() == resolved
        holder.inited == inited

        where:
        serviceId   | service           | resolved  | inited
        "1"         | Mock(Object)      | true      | false
        "2"         | Mock(IService)    | true      | false
    }

    def "Test init service on a normal service"() {
        given:
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(service, serviceId, mockHook)
        mockHook.isSatisfied(service) >> true

        when:
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.service == service
        holder.isSatisfied() == resolved
        holder.inited == inited

        where:
        serviceId   | service           | resolved  | inited
        "1"         | Mock(Object)      | true      | true
        "2"         | Mock(IService)    | true      | true
    }

        def "Test init service which is IInitial instance"() {
        given:
        IInitial initialSvc = Mock(IInitial)
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(initialSvc, serviceId, mockHook)
        mockHook.isSatisfied(initialSvc) >> true

        when:
        holder.id == serviceId
        holder.service == initialSvc
        holder.isSatisfied() == resolved
        holder.tryInitService()

        then:
        1 * initialSvc.init()

        where:
        serviceId   | resolved  | inited
        "3"         | true      | true
    }

    def "Test service with dependency"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        ISatisfyHook mockHook = Mock(ISatisfyHook)
        ServiceHolder holder = new ServiceHolder(injectableSvc, serviceId, ["dep01", "dep02"] as String[], mockHook)
        mockHook.isSatisfied(injectableSvc) >> true

        when:
        holder.isDependsOn("dep01")

        then:
        holder.id == serviceId

        where:
        serviceId   | resolved  | inited    | dependId
        "1"         | false     | false     | "dep01"
    }

    def "Test set service dependency"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        ServiceHolder dependSvc = Mock(ServiceHolder) {
            getId() >> 'dep01'
            isSatisfied() >> true
            getService() >> new Object()
        }
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(injectableSvc) >> true
        }
        ServiceHolder holder = new ServiceHolder(injectableSvc, serviceId, ["dep01"] as String[], mockHook)

        when:
        holder.setDependency(dependSvc)
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.satisfied == resolved
        holder.inited == inited
        1 * injectableSvc.injectObject(_)

        where:
        serviceId   | resolved  | inited
        "1"         | true      | true
    }

    def "Test depends on a service factory"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        IServiceFactory svcFactory = Mock(IServiceFactory) {
            createService(injectableSvc) >> new Object()
        }
        ServiceHolder dependSvc = Mock(ServiceHolder) {
            getId() >> 'dep01'
            isSatisfied() >> true
            getService() >> svcFactory
        }
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(injectableSvc) >> true
        }
        ServiceHolder holder = new ServiceHolder(injectableSvc, serviceId, ["dep01"] as String[], mockHook)

        when:
        holder.setDependency(dependSvc)
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.satisfied == resolved
        holder.inited == inited
        1 * injectableSvc.injectObject(_)

        where:
        serviceId   | resolved  | inited
        "1"         | true      | true
    }
}
