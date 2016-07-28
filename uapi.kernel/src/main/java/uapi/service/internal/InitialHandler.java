/**
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
import uapi.annotation.*;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.service.IInitial;
import uapi.service.annotation.Init;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * A handler used to handle IInitial related annotations
 */
@AutoService(IAnnotationsHandler.class)
public final class InitialHandler extends AnnotationsHandler {

    private static final String METHOD_INIT_NAME    = "init";
//    private static final String METHOD_LAZY_NAME    = "lazy";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { Init.class };

    @Override
    public Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    public void handleAnnotatedElements(
            final IBuilderContext builderCtx,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> methodElements
    ) throws KernelException {
        ArgumentChecker.equals(annotationType, Init.class, "annotationType");

        builderCtx.getLogger().info("Start processing Init annotation");
        methodElements.forEach(methodElement -> {
            if (methodElement.getKind() != ElementKind.METHOD) {
                throw new KernelException(
                        "The Init annotation only can be applied on method",
                        methodElement.getSimpleName().toString());
            }
            checkModifiers(methodElement, Init.class, Modifier.PRIVATE, Modifier.STATIC);
            Element classElemt = methodElement.getEnclosingElement();
            checkModifiers(classElemt, Init.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            MethodMeta.Builder methodBuilder = MethodMeta.builder(methodElement, builderCtx);
            if (methodBuilder.getParameterCount() > 0) {
                throw new KernelException(
                        "The method [{}:{}] with Init annotation can not has any parameter",
                        classElemt.getSimpleName().toString(),
                        methodElement.getSimpleName().toString());
            }

            String methodName = methodElement.getSimpleName().toString();
            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            List<MethodMeta.Builder> existing = clsBuilder.findMethodBuilders(METHOD_INIT_NAME);
            if (existing.size() > 0) {
                throw new KernelException(
                        "Multiple Init annotation was defined in the class {}",
                        classElemt.getSimpleName().toString());
            }
            String initCode = StringHelper.makeString("super.{}();", methodName);
//            Init init = methodElement.getAnnotation(Init.class);
//            String lazyCode = StringHelper.makeString("return {};", init.lazy());
            clsBuilder
                    .addImplement(IInitial.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addModifier(Modifier.PUBLIC)
                            .setName(METHOD_INIT_NAME)
                            .addAnnotationBuilder(AnnotationMeta.builder()
                                    .setName("Override"))
                            .addCodeBuilder(CodeMeta.builder()
                                    .addRawCode(initCode))
                            .setReturnTypeName("void"));
//                    .addMethodBuilder(MethodMeta.builder()
//                            .addModifier(Modifier.PUBLIC)
//                            .setName(METHOD_LAZY_NAME)
//                            .addAnnotationBuilder(AnnotationMeta.builder()
//                                    .setName("Override"))
//                            .addCodeBuilder(CodeMeta.builder()
//                                    .addRawCode(lazyCode))
//                            .setReturnTypeName(Type.BOOLEAN));
        });
    }
}
