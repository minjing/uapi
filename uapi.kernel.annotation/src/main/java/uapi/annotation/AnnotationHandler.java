package uapi.annotation;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;

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

    public abstract void handle(
            final IBuilderContext builderCtx
    ) throws KernelException;

    protected void checkModifiers(
            final Element element,
            final Modifier... unexpectedModifiers
    ) throws KernelException {
        Set<Modifier> existingModifiers = element.getModifiers();
        if (CollectionHelper.contains(existingModifiers, unexpectedModifiers)) {
            throw new KernelException(
                    "The {} element [{}.{}] has NotNull annotation must not be private, static or final",
                    element.getKind(),
                    element.getEnclosingElement().getSimpleName().toString(),
                    element.getSimpleName().toString());
        }
    }
}
