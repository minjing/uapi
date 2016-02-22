package uapi.service.internal;

import uapi.KernelException;
import uapi.annotation.AnnotationHandler;
import uapi.annotation.BuilderContext;
import uapi.service.annotation.Init;

import javax.annotation.processing.RoundEnvironment;

/**
 * A handler used to handle Init annotation
 */
public final class InitHandler extends AnnotationHandler<Init> {

    @Override
    public Class<Init> getSupportAnnotationType() {
        return Init.class;
    }

    @Override
    public void handle(
            final RoundEnvironment roundEnv,
            final BuilderContext buildCtx
    ) throws KernelException {
        return;
    }
}
