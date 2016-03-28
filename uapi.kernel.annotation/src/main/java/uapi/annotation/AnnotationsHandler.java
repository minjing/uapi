package uapi.annotation;

import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.Pair;
import uapi.helper.StringHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Default implementation for handle related annotations
 */
public abstract class AnnotationsHandler implements IAnnotationsHandler {

    private LogSupport _logger;

    @Override
    public Class<? extends Annotation>[] getSupportedAnnotations() {
        return getOrderedAnnotations();
    }

    protected void checkModifiers(
            final Element element,
            final Class<? extends Annotation> annotation,
            final Modifier... unexpectedModifiers
    ) throws KernelException {
        Set<Modifier> existingModifiers = element.getModifiers();
        Modifier unsupportedModifier = CollectionHelper.contains(existingModifiers, unexpectedModifiers);
        if (unsupportedModifier != null) {
            throw new KernelException(
                    "The {} element [{}.{}] with {} annotation must not be {}",
                    element.getKind(),
                    element.getEnclosingElement().getSimpleName().toString(),
                    element.getSimpleName().toString(),
                    annotation.getName(),
                    unsupportedModifier);
        }
    }

    @Override
    public void handle(
            final IBuilderContext builderContext
    ) throws KernelException {
//        boolean needsHandle = false;
//        for (Class<? extends Annotation> annotationType : getSupportedAnnotations()) {
//            Set elements = builderContext.getElementsAnnotatedWith(annotationType);
//            builderContext.getLogger().info("=========" + annotationType + " >>> " + elements);
//            if (elements != null && elements.size() > 0) {
//                needsHandle = true;
//                break;
//            }
//        }
//        if (needsHandle) {
            Observable.from(getOrderedAnnotations())
                    .map((annotation) -> new Pair<>(annotation, builderContext.getElementsAnnotatedWith(annotation)))
                    .subscribe(pair -> handleAnnotatedElements(builderContext, pair.getLeftValue(), pair.getRightValue()),
                            (t) -> builderContext.getLogger().error(t));
//        }
    }

    protected abstract Class<? extends Annotation>[] getOrderedAnnotations();

    /**
     * Handle specified annotation on specified annotated elements
     *
     * @param   builderContext
     *          The context for building object
     * @param   annotationType
     *          The annotation type which is applied on the elements
     * @param   elements
     *          The elements which was annotated
     * @throws  KernelException
     *          Handle elements failed
     */
    protected abstract void handleAnnotatedElements(
            final IBuilderContext builderContext,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException;

    @Override
    public String toString() {
        return StringHelper.makeString("AnnotationsHandler[supportedAnnotations={}]",
                CollectionHelper.asString(getSupportedAnnotations()));
    }
}
