package uapi.service.internal;

import com.sun.org.apache.xpath.internal.Arg;
import uapi.KernelException;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A handler is used for handling IInjectableService related annotations
 */
public class InjectableServiceHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] { Inject.class, Optional.class };

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
        ArgumentChecker.notNull(annotationType, "annotationType");

        if (annotationType.equals(Inject.class)) {

        } else if (annotationType.equals(Optional.class)) {

        } else {
            throw new KernelException("Unsupported annotation - {}", annotationType.getClass().getName());
        }
    }
}
