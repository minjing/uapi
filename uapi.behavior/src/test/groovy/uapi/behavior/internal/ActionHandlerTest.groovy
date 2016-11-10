package uapi.behavior.internal

import spock.lang.Ignore
import spock.lang.Specification
import uapi.annotation.IBuilderContext
import uapi.behavior.IAction
import uapi.behavior.IExecutionContext
import uapi.behavior.annotation.Action

/**
 * Unit test for AnnotationHandler
 */
@Ignore
class ActionHandlerTest extends Specification {

    def 'Test handle'() {
        given:
        def buildCtx = Mock(IBuilderContext)
        def actionAnno = TestAction.class.getAnnotation(Action.class)


    }

    @Action("Test")
    abstract class TestAction implements IAction<Integer, String> {

        @Override
        public String process(Integer input, IExecutionContext ctx) {
            return input.toString();
        }
    }
}
