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
        holder.inited == inited

        where:
        serviceId   | from                          | service           | resolved  | inited
        "1"         | QualifiedServiceId.FROM_LOCAL | Mock(Object)      | true      | false
        "2"         | QualifiedServiceId.FROM_LOCAL | Mock(IService)    | true      | false
    }

    def "Test init service on a normal service"() {
        given:
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true
        }
        ServiceHolder holder = new ServiceHolder(from, service, serviceId, mockHook)

        when:
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.service == service
        holder.inited == inited

        where:
        serviceId   | from                          | service           | resolved  | inited
        "1"         | QualifiedServiceId.FROM_LOCAL | Mock(Object)      | true      | true
        "2"         | QualifiedServiceId.FROM_LOCAL | Mock(IService)    | true      | true
    }

        def "Test init service which is IInitial instance"() {
        given:
        IInitial initialSvc = Mock(IInitial)
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true;
        }
        ServiceHolder holder = new ServiceHolder(from, initialSvc, serviceId, mockHook)

        when:
        holder.id == serviceId
        holder.service == initialSvc
        holder.tryInitService()

        then:
        1 * initialSvc.init()

        where:
        serviceId   | from                          | resolved  | inited
        "3"         | QualifiedServiceId.FROM_LOCAL | true      | true
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
        serviceId   | from                          | resolved  | inited    | dependId
        "1"         | QualifiedServiceId.FROM_LOCAL | false     | false     | "dep01"
    }

    def "Test set service dependency"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        ServiceHolder dependSvc = Mock(ServiceHolder) {
            getId() >> 'dep01'
            getQualifiedId() >> QualifiedServiceId.splitTo('dep01@Local')
            getService() >> new Object()
            tryInitService() >> true
        }
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true
        }
        ServiceHolder holder = new ServiceHolder(from, injectableSvc, serviceId, ["dep01@Local"] as String[], mockHook)

        when:
        holder.setDependency(dependSvc)
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.inited == inited
        1 * injectableSvc.injectObject(_)

        where:
        serviceId   | from                          | resolved  | inited
        "1"         | QualifiedServiceId.FROM_LOCAL | true      | true
    }

    def "Test depends on a service factory"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        IServiceFactory svcFactory = Mock(IServiceFactory) {
            createService(injectableSvc) >> new Object()
        }
        ServiceHolder dependSvc = Mock(ServiceHolder) {
            getId() >> 'dep01'
            getQualifiedId() >> QualifiedServiceId.splitTo('dep01@Local')
            getService() >> svcFactory
            tryInitService() >> true
        }
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true
        }
        ServiceHolder holder = new ServiceHolder(from, injectableSvc, serviceId, ["dep01@Local"] as String[], mockHook)

        when:
        holder.setDependency(dependSvc)
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.inited == inited
        1 * injectableSvc.injectObject(_)

        where:
        serviceId   | from                          | resolved  | inited
        "1"         | QualifiedServiceId.FROM_LOCAL | true      | true
    }

    def "Test getUnresolvedServices method"() {
        given:
        IInjectable injectableSvc = Mock(IInjectable)
        ISatisfyHook mockHook = Mock(ISatisfyHook) {
            isSatisfied(_) >> true
        }
        ServiceHolder holder = new ServiceHolder(from, injectableSvc, serviceId, ["dep01@Local"] as String[], mockHook)

        when:
        holder.tryInitService()

        then:
        holder.id == serviceId
        holder.inited == inited
        holder.getUnresolvedServices('Local') == ['dep01']

        where:
        serviceId   | from                          | inited
        "1"         | QualifiedServiceId.FROM_LOCAL | false
    }
}
