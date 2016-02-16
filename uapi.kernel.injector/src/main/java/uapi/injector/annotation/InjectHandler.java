package uapi.injector.annotation;

import uapi.KernelException;
import uapi.annotation.AnnotationHandler;
import uapi.annotation.BuilderContext;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.Set;

/**
 * Created by min on 16/2/16.
 */
public class InjectHandler extends AnnotationHandler<Inject> {

    @Override
    public Class<Inject> getSupportAnnotationType() {
        return Inject.class;
    }

    @Override
    public void handle(
            final RoundEnvironment roundEnv,
            final BuilderContext buildCtx
    ) throws KernelException {
        getLogger().info("IN InjectHandler!!!!");
        Set<? extends Element> paramElements = roundEnv.getElementsAnnotatedWith(Inject.class);
        if (paramElements.size() == 0) {
            return;
        }
        paramElements.forEach(paramElement -> {
            if (paramElement.getKind() != ElementKind.FIELD) {
                throw new KernelException(
                        "The Inject annotation only can be applied on field",
                        paramElement.getSimpleName().toString());
            }
        });
    }
}
