/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation;

import freemarker.template.Template;
import uapi.KernelException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * A class file builder context
 */
public interface IBuilderContext {

    ProcessingEnvironment getProcessingEnvironment();

    RoundEnvironment getRoundEnvironment();

    LogSupport getLogger();

    Elements getElementUtils();

    Types getTypeUtils();

    Filer getFiler();

    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> annotationType);

    List<ClassMeta.Builder> getBuilders();

    void clearBuilders();

    Template loadTemplate(String templatePath);

    ClassMeta.Builder findClassBuilder(Element classElement);

    void putHelper(IHandlerHelper helper);

    IHandlerHelper getHelper(String name);

    void checkModifiers(
            final Element element,
            final Class<? extends Annotation> annotation,
            final Modifier... unexpectedModifiers
    ) throws KernelException;

    void checkAnnotations(
            final Element element,
            final Class<? extends Annotation>... annotationTypes
    ) throws KernelException;

    Element findFieldWith(
            final Element classElement,
            final Class<?> fieldType,
            final Class annotationType);

    /**
     * Check specific class element can be assigned to specified type
     *
     * @param   classElement
     *          The class element which will be checked
     * @param   type
     *          The class type
     * @return  true means the element can be assigned to the type otherwise return false
     */
    boolean isAssignable(
            final Element classElement,
            final Class type);

    /**
     * Check type1 can be assigned to type2
     *
     * @param   type1
     *          The first type
     * @param   type2
     *          The second type
     * @return  True means the first type can be assigned to second type, otherwise return false
     */
    boolean isAssignable(final String type1, final Class type2);

    /**
     * Check type1 can be assigned to type2
     *
     * @param   type1
     *          The first type
     * @param   type2
     *          The second type
     * @return  True means the first type can be assigned to second type, otherwise return false
     */
    boolean isAssignable(final String type1, final String type2);
}
