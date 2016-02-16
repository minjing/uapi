package uapi.annotation;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;

import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.Annotation;

/**
 * Created by min on 16/2/10.
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
            final RoundEnvironment roundEnv,
            final BuilderContext buildCtx
    ) throws KernelException;
}
