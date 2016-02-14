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
 * Created by min on 16/2/10.
 */
final class LogSupport {

    private final ProcessingEnvironment _procEnv;
    private final Messager _msger;

    public LogSupport(final ProcessingEnvironment processingEnvironment) {
        ArgumentChecker.notNull(processingEnvironment, "processingEnvironment");
        this._procEnv = processingEnvironment;
        this._msger = this._procEnv.getMessager();
    }

    public void info(String msg) {
        this._msger.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public void error(String msg, Object... args) {
        this._msger.printMessage(Diagnostic.Kind.ERROR, StringHelper.makeString(msg, args));
    }

    public void error(String msg, Element element, AnnotationMirror annotation) {
        this._msger.printMessage(Diagnostic.Kind.ERROR, msg, element, annotation);
    }

    public void error(Exception ex) {
        this._msger.printMessage(Diagnostic.Kind.ERROR, ExceptionHelper.getStackString(ex));
    }
}
