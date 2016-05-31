/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import uapi.KernelException;

import java.lang.annotation.Annotation;

/**
 * A handler for handle one or more annotations which has relationship
 */
public interface IAnnotationsHandler {

//    /**
//     * Set a LoggerSupport
//     *
//     * @param   logger
//     *          A LoggerSupport instance
//     */
//    void setLogger(final LogSupport logger);

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
