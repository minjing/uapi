/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.internal;

import com.google.auto.service.AutoService;
import uapi.KernelException;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.IBuilderContext;
import uapi.annotation.IHandlerHelper;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A handler is used for handling IInjectableService related annotations
 */
@AutoService(IAnnotationsHandler.class)
public class InjectableHandler extends AnnotationsHandler {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] { Inject.class, Optional.class };

    private final InjectParser _injectParser;
    private final OptionalParser _optionalParser;

    public InjectableHandler() {
        this._injectParser = new InjectParser();
        this._optionalParser = new OptionalParser();
    }

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
            this._injectParser.parse(builderContext, elements);
        } else if (annotationType.equals(Optional.class)) {
            this._optionalParser.parse(builderContext, elements);
        } else {
            throw new KernelException("Unsupported annotation - {}", annotationType.getClass().getName());
        }
    }

//    @Override
//    public IHandlerHelper getHelper() {
//        return this._injectParser.getHelper();
//    }
}
