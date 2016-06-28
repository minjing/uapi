/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal

import spock.lang.Specification
import uapi.service.Dependency
import uapi.service.IInjectable
import uapi.service.IRegistry
import uapi.service.ISatisfyHook
import uapi.service.IService
import uapi.service.IServiceLoader
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
        registry.findService("1", QualifiedServiceId.FROM_LOCAL) == svc1
        registry.findService("2", QualifiedServiceId.FROM_LOCAL) == svc1
        registry.findService("3", QualifiedServiceId.FROM_LOCAL) == svc2
        registry.findService("4", QualifiedServiceId.FROM_LOCAL) == svc2
        registry.getCount() == 4
    }

    static interface IInjectableService extends IService, IInjectable {}

    def 'Test getUnresolvedServices method'() {
        def svc = Mock(IInjectableService) {
            getIds() >> ["1"]
            getDependencies() >> [dependQSvcId]
        }
        def svcLoader = Mock(IServiceLoader) {
            getName() >> "Test"
            load(dependSvcId, Object.class) >> dependSvc
        }

        when:
        registry.register(svc)
        registry.registerServiceLoader(svcLoader)

        then:
        1 * svcLoader.load(dependSvcId, Object.class)

        where:
        dependSvcId | dependQSvcId                              | dependSvc
        'd1'        | new Dependency('d1@Test', Object.class)   | 'abc'
        'd2'        | new Dependency('d2@Any', Object.class)    | 'abc'
    }
}
