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
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import java.lang.annotation.Annotation;

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
