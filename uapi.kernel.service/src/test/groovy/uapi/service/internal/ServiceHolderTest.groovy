package uapi.service.internal

import spock.lang.Specification
import uapi.service.IInitial
import uapi.service.IService

/**
 * Test case for ServiceHolder
 */
class ServiceHolderTest extends Specification {

    def "Test add a service"() {
        given:
        ServiceHolder holder = new ServiceHolder(service, serviceId)
        expect:
        holder.id == serviceId
        holder.service == service
        holder.isResolved() == resolved
        holder.inited == inited

        where:
        serviceId   | service           | resolved  | inited
        "1"         | Mock(Object)      | true      | false
        "2"         | Mock(IService)    | true      | false
    }

    def "Test init service on a normal service"() {
        given:
        ServiceHolder holder = new ServiceHolder(service, serviceId)

        when:
        holder.initService()

        then:
        holder.id == serviceId
        holder.service == service
        holder.isResolved() == resolved
        holder.inited == inited

        where:
        serviceId   | service           | resolved  | inited
        "1"         | Mock(Object)      | true      | true
        "2"         | Mock(IService)    | true      | true
    }

    IInitial initialSvc = Mock(IInitial)

    def "Test init service which is IInitial instance"() {
        given:
        ServiceHolder holder = new ServiceHolder(initialSvc, serviceId)

        when:
        holder.id == serviceId
        holder.service == initialSvc
        holder.isResolved() == resolved
        holder.initService()

        then:
        1 * initialSvc.init()

        where:
        serviceId   | resolved  | inited
        "3"         | true      | true
    }
}
