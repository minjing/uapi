package uapi.service.internal

import spock.lang.Specification
import uapi.service.IInitial
import uapi.service.IInjectable
import uapi.service.IService
import uapi.service.IServiceFactory

/**
 * Test case for ServiceHolder
 */
class ServiceHolderTest extends Specification {

//    def "Test add a service"() {
//        given:
//        ServiceHolder holder = new ServiceHolder(service, serviceId)
//
//        expect:
//        holder.id == serviceId
//        holder.service == service
//        holder.isSatisfied() == resolved
//        holder.inited == inited
//
//        where:
//        serviceId   | service           | resolved  | inited
//        "1"         | Mock(Object)      | true      | false
//        "2"         | Mock(IService)    | true      | false
//    }
//
//    def "Test init service on a normal service"() {
//        given:
//        ServiceHolder holder = new ServiceHolder(service, serviceId)
//
//        when:
//        holder.initService()
//
//        then:
//        holder.id == serviceId
//        holder.service == service
//        holder.isSatisfied() == resolved
//        holder.inited == inited
//
//        where:
//        serviceId   | service           | resolved  | inited
//        "1"         | Mock(Object)      | true      | true
//        "2"         | Mock(IService)    | true      | true
//    }
//
//    IInitial initialSvc = Mock(IInitial)
//
//    def "Test init service which is IInitial instance"() {
//        given:
//        ServiceHolder holder = new ServiceHolder(initialSvc, serviceId)
//
//        when:
//        holder.id == serviceId
//        holder.service == initialSvc
//        holder.isSatisfied() == resolved
//        holder.initService()
//
//        then:
//        1 * initialSvc.init()
//
//        where:
//        serviceId   | resolved  | inited
//        "3"         | true      | true
//    }
//
//    IInjectable injectableSvc = Mock(IInjectable)
//
//    def "Test service with dependency"() {
//        given:
//        ServiceHolder holder = new ServiceHolder(injectableSvc, serviceId, ["dep01", "dep02"] as String[])
//
//        when:
//        holder.isDependsOn("dep01")
//
//        then:
//        holder.id == serviceId
//
//        where:
//        serviceId   | resolved  | inited    | dependId
//        "1"         | false     | false     | "dep01"
//    }
//
//    ServiceHolder dependSvc = Mock(ServiceHolder)
//
//    def "Test set service dependency"() {
//        given:
//        ServiceHolder holder = new ServiceHolder(injectableSvc, serviceId, ["dep01"] as String[])
//
//        when:
//        holder.setDependency(dependSvc)
//        holder.initService()
//
//        then:
//        dependSvc.getId() >> "dep01"
//        dependSvc.satisfied >> true
//        dependSvc.getService() >> new Object()
//        holder.id == serviceId
//        holder.satisfied == resolved
//        holder.inited == inited
//        1 * injectableSvc.injectObject(_)
//
//        where:
//        serviceId   | resolved  | inited
//        "1"         | true      | true
//    }
//
//    IServiceFactory svcFactory = Mock(IServiceFactory)
//
//    def "Test depends on a service factory"() {
//        given:
//        ServiceHolder holder = new ServiceHolder(injectableSvc, serviceId, ["dep01"] as String[])
//
//        when:
//        holder.setDependency(dependSvc)
//        holder.initService()
//
//        then:
//        with(dependSvc) {
//            dependSvc.getId() >> "dep01"
//            dependSvc.satisfied >> true
//            dependSvc.getService() >> svcFactory
//        }
//        svcFactory.createService(injectableSvc) >> new Object()
//        holder.id == serviceId
//        holder.satisfied == resolved
//        holder.inited == inited
//        1 * injectableSvc.injectObject(_)
//
//        where:
//        serviceId   | resolved  | inited
//        "1"         | true      | true
//    }
}
