/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import uapi.helper.ArgumentChecker;
import uapi.helper.ExceptionHelper;
import uapi.helper.StringHelper;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Support log in compile time
 */
public class LogSupport {

    private final Messager _msger;

    public LogSupport(final ProcessingEnvironment processingEnvironment) {
        ArgumentChecker.notNull(processingEnvironment, "processingEnvironment");
        this._msger = processingEnvironment.getMessager();
    }

    public void info(String msg) {
        this._msger.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public void info(String msg, Object... args) {
        this._msger.printMessage(Diagnostic.Kind.NOTE, StringHelper.makeString(msg, args));
    }

    public void error(String msg, Object... args) {
        this._msger.printMessage(Diagnostic.Kind.ERROR, StringHelper.makeString(msg, args));
    }

    public void error(String msg, Element element, AnnotationMirror annotation) {
        this._msger.printMessage(Diagnostic.Kind.ERROR, msg, element, annotation);
    }

    public void error(Throwable t) {
        this._msger.printMessage(Diagnostic.Kind.ERROR, ExceptionHelper.getStackString(t));
    }
}
