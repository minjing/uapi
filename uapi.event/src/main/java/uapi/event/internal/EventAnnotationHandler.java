package uapi.event.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.event.annotation.Event;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A annotation handler for Event annotation
 */
@AutoService(IAnnotationsHandler.class)
public class EventAnnotationHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] { Event.class };

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
