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
import freemarker.template.Template;
import uapi.KernelException;
import uapi.Type;
import uapi.annotation.*;
import uapi.rx.Looper;
import uapi.service.ITagged;
import uapi.service.annotation.Tag;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handler used to handle Tag annotation
 */
@AutoService(IAnnotationsHandler.class)
public class TagHandler extends AnnotationsHandler {

    private static final String TEMPLATE_GET_TAGS   = "template/getTags_method.ftl";
    private static final String VAR_TAGS            = "tags";

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends Annotation>[] getOrderedAnnotations() {
        return new Class[] { Tag.class };
    }

    @Override
    protected void handleAnnotatedElements(
            final IBuilderContext builderContext,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException {
        Looper.from(elements).foreach(classElement -> {
            if (classElement.getKind() != ElementKind.CLASS) {
                throw new KernelException(
                        "The Tag annotation only can be applied on class - {}",
                        classElement.getSimpleName().toString());
            }
            builderContext.checkModifiers(classElement, Tag.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            Template tempGetIds = builderContext.loadTemplate(TEMPLATE_GET_TAGS);
            Tag tag = classElement.getAnnotation(Tag.class);
            Map<String, String[]> modelGetTags = new HashMap<>();
            modelGetTags.put(VAR_TAGS, tag.value());

            ClassMeta.Builder classBuilder = builderContext.findClassBuilder(classElement);
            classBuilder
                    .addImplement(ITagged.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder()
                                    .setName(AnnotationMeta.OVERRIDE))
                            .setName(ITagged.METHOD_GETTAGS)
                            .addModifier(Modifier.PUBLIC)
                            .setReturnTypeName(Type.STRING_ARRAY)
                            .addCodeBuilder(CodeMeta.builder()
                                    .setTemplate(tempGetIds)
                                    .setModel(modelGetTags)));
        });
    }
}
