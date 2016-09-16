package uapi.service.internal

import freemarker.template.Template
import spock.lang.Specification
import uapi.KernelException
import uapi.annotation.ClassMeta
import uapi.annotation.IBuilderContext
import uapi.service.ITagged
import uapi.service.annotation.Tag

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import java.lang.annotation.Annotation

/**
 * Unit test for TagHandler
 */
class TagHandlerTest extends Specification {

    def 'Test handleAnnotatedElements on incorrect element type'() {
        given:
        def builderCtx = Mock(IBuilderContext)
        def element = Mock(Element) {
            getKind() >> elementType
            getSimpleName() >> Mock(Name)
        }
        def elements = new HashSet<>()
        elements.add(element)
        def handler = new TagHandlerTester()

        when:
        handler.handle(builderCtx, Tag.class, elements)

        then:
        thrown(ex)

        where:
        elementType                 | ex
        ElementKind.INTERFACE       | KernelException
        ElementKind.ANNOTATION_TYPE | KernelException
        ElementKind.CONSTRUCTOR     | KernelException
        ElementKind.ENUM            | KernelException
        ElementKind.FIELD           | KernelException
        ElementKind.METHOD          | KernelException
        ElementKind.PACKAGE         | KernelException
        ElementKind.TYPE_PARAMETER  | KernelException
    }

    def 'Test handleAnnotationElements'() {
        given:
        def element = Mock(Element) {
            getKind() >> ElementKind.CLASS
            getSimpleName() >> Mock(Name)
            getAnnotation(_ as Class) >> (Tag) TagHandlerTester.class.getAnnotation(Tag.class)
        }
        def elements = new HashSet<>()
        elements.add(element)
        def classBuilder = Mock(ClassMeta.Builder)
        def builderCtx = Mock(IBuilderContext) {
            1 * checkModifiers(_ as Element, Tag.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            loadTemplate(_ as String) >> Mock(Template)
            findClassBuilder(element) >> classBuilder
        }
        def handler = new TagHandlerTester()

        when:
        handler.handle(builderCtx, Tag.class, elements)

        then:
        noExceptionThrown()
        1 * classBuilder.addImplement(ITagged.class.getCanonicalName()) >> classBuilder
    }

    @Tag('a')
    final class TagHandlerTester extends TagHandler {

        private void handle(
                final IBuilderContext builderContext,
                final Class<? extends Annotation> annotationType,
                final Set<? extends Element> elements
        ) throws KernelException {
            super.handleAnnotatedElements(builderContext, annotationType, elements);
        }
    }
}
