package uapi.annotation;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A generic annotation handler.
 */
public abstract class AnnotationHandler<T extends Annotation> {

    private LogSupport _logger;

    public void setLogger(final LogSupport logger) {
        ArgumentChecker.notNull(logger, "logger");
        this._logger = logger;
    }

    public LogSupport getLogger() {
        return this._logger;
    }

    public abstract Class<T> getSupportAnnotationType();

    /**
     * Indicate the handler have to invoked after specified annotations has been handled
     *
     * @return  The handled annotation class array
     */
    public  Class[] afterHandledAnnotations() {
        return new Class[0];
    }

    public abstract void handle(
            final IBuilderContext builderCtx
    ) throws KernelException;

    @Override
    public String toString() {
        return StringHelper.makeString("AnnotationHandler[supportedAnnotationType={}, afterHandledAnnotations={}]",
                getSupportAnnotationType(), CollectionHelper.asString(afterHandledAnnotations()));
    }
}
