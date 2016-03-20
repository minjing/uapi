package uapi.annotation;

import uapi.KernelException;

import java.lang.annotation.Annotation;

/**
 * A handler for handle one or more annotations which has relationship
 */
public interface IAnnotationsHandler {

    /**
     * Set a LoggerSupport
     *
     * @param   logger
     *          A LoggerSupport instance
     */
    void setLogger(final LogSupport logger);

    /**
     * Return a annotation type array which can be handled by this handler
     *
     * @return  A annotation type array which can be handled by this handler
     */
    Class<? extends Annotation>[] getSupportedAnnotations();

    /**
     * Handle all annotated element with context
     *
     * @param   builderContext
     *          The context
     * @throws  KernelException
     *          Handle annotation failed
     */
    void handle(final IBuilderContext builderContext) throws KernelException;
}
