package uapi.config.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.config.annotation.Config;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * The handler is used to handle Config related annotatioin
 */
@AutoService(IAnnotationsHandler.class)
public class ConfigHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { Config.class };

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
