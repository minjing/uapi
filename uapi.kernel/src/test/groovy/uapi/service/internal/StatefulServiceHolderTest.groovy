package uapi.service.internal

import spock.lang.Ignore
import spock.lang.Specification
import uapi.service.Dependency
import uapi.service.IInjectable
import uapi.service.ISatisfyHook
import uapi.service.IService

/**
 * Unit test for StatefulServiceHolder
 */
class StatefulServiceHolderTest extends Specification {

    def 'Test service properties'() {
        given:
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, service, serviceId, statisfyHook)

        expect:
        svcHolder.getFrom() == from
        svcHolder.id == serviceId
        svcHolder.qualifiedId == qSvcId

        where:
        from    | service   | serviceId | qSvcId                                    | statisfyHook
        'Local' | 'Service' | 'svcid'   | new QualifiedServiceId('svcid', 'Local')  | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    def 'Resolve Service with No Dependency'() {
        given:
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, service, serviceId, statisfyHook)

        when:
        boolean success = svcHolder.tryActivate()

        then:
        noExceptionThrown()
        assert success

        where:
        from    | service   | serviceId     | statisfyHook
        'Local' | 'Service' | 'svcid'       | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    def 'Activate service with dependencies'() {
        given:
        def dependencies = [
                Mock(Dependency) {
                    getServiceId() >> Mock(QualifiedServiceId) {
                        getId() >> depSvcId
                        getFrom() >> from
                    }
                }
        ] as Dependency[]
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, svc, svcId, dependencies, satisfyHook)

        when:
        svcHolder.setDependency(Mock(IServiceHolder) {
            getQualifiedId() >> new QualifiedServiceId(depSvcId, from)
            getService() >> new Object()
            getId() >> svcId
        })
        boolean success = svcHolder.tryActivate(true)

        then:
        assert success

        where:
        from    | svc               | svcId     | depSvcId  | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | 'depid'   | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    interface MockService extends IService, IInjectable { }
}
