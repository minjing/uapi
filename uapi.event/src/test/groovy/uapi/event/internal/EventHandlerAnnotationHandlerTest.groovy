package uapi.event.internal

import spock.lang.Specification
import uapi.InvalidArgumentException
import uapi.KernelException
import uapi.annotation.ClassMeta
import uapi.annotation.IBuilderContext
import uapi.event.IEventHandler
import uapi.event.annotation.Event
import uapi.event.annotation.EventHandler
import uapi.service.IServiceHandlerHelper
import uapi.service.annotation.Tag

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name

/**
 * Unit test for EventHandlerAnnotationHandler
 */
class EventHandlerAnnotationHandlerTest extends Specification {

    def 'Test handle non EventHandler annotation'() {
        given:
        EventHandlerAnnotationHandler handler = new EventHandlerAnnotationHandler()
        def builderCtx = Mock(IBuilderContext)
        def elements = Mock(Set)

        when:
        handler.handleAnnotatedElements(builderCtx, Event.class, elements)

        then:
        thrown(InvalidArgumentException)
    }

    def 'Test handle non class type element'() {
        given:
        def handler = new EventHandlerAnnotationHandler()
        def builderCtx = Mock(IBuilderContext)
        def element = Mock(Element) {
            getKind() >> elementKind
            getSimpleName() >> Mock(Name) {
                toString() >> elementName
            }
        }
        def elements = new HashSet()
        elements.add(element)

        when:
        handler.handleAnnotatedElements(builderCtx, EventHandler.class, elements)

        then:
        thrown(KernelException)

        where:
        elementKind             | elementName
        ElementKind.ENUM        | 'name'
        ElementKind.CONSTRUCTOR | 'name'
        ElementKind.FIELD       | 'name'
        ElementKind.INTERFACE   | 'name'
        ElementKind.METHOD      | 'name'
    }

    def 'Test handle element'() {
        given:
        def element = Mock(Element) {
            getKind() >> ElementKind.CLASS
            getSimpleName() >> Mock(Name) {
                toString() >> 'name'
            }
            getAnnotation(EventHandler.class) >> Test.class.getAnnotation(EventHandler)
        }
        def elements = new HashSet()
        elements.add(element)
        def classBuilder = Mock(ClassMeta.Builder)
        def svcHandlerHelper = Mock(IServiceHandlerHelper)
        def builderCtx = Mock(IBuilderContext) {
            1 * checkModifiers(_ as Element, EventHandler.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            getHelper(IServiceHandlerHelper.name) >> svcHandlerHelper
            findClassBuilder(element) >> classBuilder
        }
        def handler = new EventHandlerAnnotationHandler()

        when:
        handler.handleAnnotatedElements(builderCtx, EventHandler.class, elements)

        then:
        noExceptionThrown()
        1 * svcHandlerHelper.addServiceId(classBuilder, IEventHandler.class.getCanonicalName())
        1 * classBuilder.addImplement(_) >> classBuilder
        1 * classBuilder.addMethodBuilder(_) >> classBuilder

        where:
        modifier            | test
        Modifier.PRIVATE    | null
    }

    @EventHandler('Test')
    private class Test {}
}
