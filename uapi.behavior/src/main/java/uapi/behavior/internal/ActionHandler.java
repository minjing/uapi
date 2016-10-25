package uapi.behavior.internal;

import uapi.KernelException;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.behavior.annotation.Action;
import uapi.behavior.annotation.ActionCall;
import uapi.service.annotation.Service;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * The handler is used to handle IAction related annotations
 */
@Service(IAnnotationsHandler.class)
public class ActionHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] {Action.class, ActionCall.class };

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
