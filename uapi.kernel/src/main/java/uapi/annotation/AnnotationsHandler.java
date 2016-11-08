/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.Pair;
import uapi.helper.StringHelper;
import uapi.rx.Looper;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation for handle related annotations
 */
public abstract class AnnotationsHandler implements IAnnotationsHandler {

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

    protected void checkAnnotations(
            final Element element,
            final Class<? extends Annotation>... annotationTypes
    ) throws KernelException {
        ArgumentChecker.notNull(element, "element");
        List<Class<? extends Annotation>> unAnnotateds = Observable.from(annotationTypes)
                .filter(annotationType -> element.getAnnotation(annotationType) == null)
                .toList().toBlocking().single();
        if (unAnnotateds == null || unAnnotateds.size() > 0) {
            throw new KernelException("The {} element [{}] does not annotated with {}.",
                    element.getKind(),
                    element.getSimpleName().toString(),
                    CollectionHelper.asString(annotationTypes));
        }
    }

    protected String getTypeInAnnotation(
            final AnnotationMirror annotation,
            final String fieldName,
            final LogSupport logger
    ) {
        ArgumentChecker.notNull(annotation, "annotation");
        ArgumentChecker.notEmpty(fieldName, "fieldName");
        List<String> types = Observable.from(annotation.getElementValues().entrySet())
                .filter(entry -> fieldName.equals(entry.getKey().getSimpleName().toString()))
                .map(Map.Entry::getValue)
                .map(annoValue -> (DeclaredType) annoValue.getValue())
                .map(declaredType -> (TypeElement) declaredType.asElement())
                .map(typeElem -> typeElem.getQualifiedName().toString())
                .toList().toBlocking().single();
        if (types == null || types.size() == 0) {
            return null;
        } else if (types.size() == 1) {
            return types.get(0);
        } else {
            throw new KernelException("Found more than one value are defined in annotation {} - {}",
                    annotation.getAnnotationType().toString(), types);
        }
    }

    /**
     * The utility method for getting class object which is defined in Annotation
     *
     * @param   annotation
     *          The annotation
     * @param   fieldName
     *          The field which hold class object
     * @return  The class object list
     */
    @SuppressWarnings("unchecked")
    protected List<String> getTypesInAnnotation(
            final AnnotationMirror annotation,
            final String fieldName,
            final LogSupport logger) {
        ArgumentChecker.notNull(annotation, "annotation");
        ArgumentChecker.notEmpty(fieldName, "fieldName");
        List<String> types = new ArrayList<>();
        Observable.from(annotation.getElementValues().entrySet())
                .filter(entry -> fieldName.equals(entry.getKey().getSimpleName().toString()))
                .map(Map.Entry::getValue)
                .flatMap(annoValue -> Observable.from((List<AnnotationValue>) annoValue.getValue()))
                .map(annoValue -> (DeclaredType) annoValue.getValue())
                .map(declaredType -> (TypeElement) declaredType.asElement())
                .map(typeElem -> typeElem.getQualifiedName().toString())
                .subscribe(types::add, logger::error);
        return types;
    }

    @Override
    public void handle(
            final IBuilderContext builderContext
    ) throws KernelException {
            Observable.from(getOrderedAnnotations())
                    .map((annotation) -> new Pair<>(annotation, builderContext.getElementsAnnotatedWith(annotation)))
                    .subscribe(pair -> handleAnnotatedElements(builderContext, pair.getLeftValue(), pair.getRightValue()),
                            (t) -> builderContext.getLogger().error(t));
    }

    protected abstract Class<? extends Annotation>[] getOrderedAnnotations();

    public IHandlerHelper getHelper() {
        return null;
    }

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
