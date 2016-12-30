package uapi.service.internal

import spock.lang.Specification
import uapi.KernelException
import uapi.service.Dependency
import uapi.service.IInitial
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

    def 'Activate service with unset dependencies'() {
        given:
        def dependency = Mock(Dependency) {
            getServiceId() >> Mock(QualifiedServiceId) {
                getId() >> depSvcId
                getFrom() >> from
            }
        }
        def dependencies = [dependency] as Dependency[]
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, svc, svcId, dependencies, satisfyHook)

        when:
        def unsetDependencies = svcHolder.getUnsetDependencies()

        then:
        assert unsetDependencies.size() == 1
        assert unsetDependencies.get(0) == dependency

        where:
        from    | svc               | svcId     | depSvcId  | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | 'depid'   | Mock(ISatisfyHook) { isSatisfied(_) >> true }
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

    def 'Activate initial service with dependencies'() {
        given:
        def dependencies = [
                Mock(Dependency) {
                    getServiceId() >> Mock(QualifiedServiceId) {
                        getId() >> depSvcId
                        getFrom() >> from
                    }
                }
        ] as Dependency[]
        def mockSvc = Mock(MockService)
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, mockSvc, svcId, dependencies, satisfyHook)

        when:
        svcHolder.setDependency(Mock(IServiceHolder) {
            getQualifiedId() >> new QualifiedServiceId(depSvcId, from)
            getService() >> svc
            getId() >> svcId
        })
        boolean success = svcHolder.tryActivate(true)

        then:
        assert success
        1 * mockSvc.init()

        where:
        from    | svc               | svcId     | depSvcId  | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | 'depid'   | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    def 'Activate service when satisfy failed'() {
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
        boolean success = svcHolder.tryActivate(false)

        then:
        assert ! success

        where:
        from    | svc               | svcId     | depSvcId  | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | 'depid'   | Mock(ISatisfyHook) { isSatisfied(_) >> false }
    }

    def 'Throw exception when activate service when satisfy failed'() {
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
        svcHolder.tryActivate(true)

        then:
        thrown(KernelException)

        where:
        from    | svc               | svcId     | depSvcId  | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | 'depid'   | Mock(ISatisfyHook) { isSatisfied(_) >> false }
    }

    def 'Test do inject before resolved'() {
        given:
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, svc, svcId, satisfyHook)

        when:
        svcHolder.inject()

        then:
        noExceptionThrown()
        assert svcHolder.isResolved()
        assert svcHolder.isInjected()

        where:
        from    | svc               | svcId     | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    def 'Test do satisfy before resolved'() {
        given:
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, svc, svcId, satisfyHook)

        when:
        svcHolder.satisfy()

        then:
        noExceptionThrown()
        assert svcHolder.isResolved()
        assert svcHolder.isInjected()
        assert svcHolder.isSatisfied()

        where:
        from    | svc               | svcId     | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    def 'Test do activate before resolved'() {
        given:
        StatefulServiceHolder svcHolder = new StatefulServiceHolder(from, svc, svcId, satisfyHook)

        when:
        svcHolder.activate()

        then:
        noExceptionThrown()
        assert svcHolder.isResolved()
        assert svcHolder.isInjected()
        assert svcHolder.isSatisfied()
        assert svcHolder.isActivated()

        where:
        from    | svc               | svcId     | satisfyHook
        'Local' | Mock(MockService) | 'svcid'   | Mock(ISatisfyHook) { isSatisfied(_) >> true }
    }

    interface MockService extends IService, IInjectable, IInitial { }
}
