package uapi.behavior.internal;

import uapi.KernelException;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.behavior.annotation.EventBehavior;
import uapi.service.annotation.Service;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * The annotation handler will generate class which implement
 */
@Service(IAnnotationsHandler.class)
public class EventBehaviorHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] {
            uapi.behavior.annotation.Execution.class, EventBehavior.class
    };

    @Override
    protected Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    protected void handleAnnotatedElements(
            final IBuilderContext builderContext,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException {

    }
}
