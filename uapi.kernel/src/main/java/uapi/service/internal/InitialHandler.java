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
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.IInitial;
import uapi.service.IInitialHandlerHelper;
import uapi.service.annotation.Init;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * A handler used to handle IInitial related annotations
 */
@AutoService(IAnnotationsHandler.class)
public final class InitialHandler extends AnnotationsHandler {

    private static final String METHOD_INIT_NAME    = "init";

    private static final String TEMP_INIT           = "template/init_method.ftl";
    private static final String MODEL_INIT          = "ModelInit";
    private static final String VAR_METHODS         = "methods";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { Init.class };

    private final InitialHandlerHelper _helper;

    public InitialHandler() {
        this._helper = new InitialHandlerHelper();
    }

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
            this._helper.addInitMethod(builderCtx, clsBuilder, methodName);
//            Template tempInit = builderCtx.loadTemplate(TEMP_INIT);
//            clsBuilder
//                    .addImplement(IInitial.class.getCanonicalName())
//                    .addMethodBuilder(MethodMeta.builder()
//                            .addModifier(Modifier.PUBLIC)
//                            .setName(METHOD_INIT_NAME)
//                            .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
//                            .addCodeBuilder(CodeMeta.builder()
//                                    .setTemplate(tempInit)
//                                    .setModel(clsBuilder.getTransience(MODEL_INIT)))
//                            .setReturnTypeName(Type.VOID));
        });
    }

    @Override
    public IHandlerHelper getHelper() {
        return this._helper;
    }

    private class InitialHandlerHelper implements IInitialHandlerHelper {

        @Override
        public String getName() {
            return IInitialHandlerHelper.name;
        }

        @Override
        public void addInitMethod(
                final IBuilderContext builderContext,
                final ClassMeta.Builder classBuilder,
                final String... methodNames) {
            ArgumentChecker.required(builderContext, "builderContext");
            ArgumentChecker.required(classBuilder, "classBuilder");
            ArgumentChecker.required(methodNames, "methodNames");

            Map<String, Object> tempInitModel = classBuilder.createTransienceIfAbsent(MODEL_INIT, HashMap::new);
            String[] methods = (String[]) tempInitModel.get(VAR_METHODS);
            List<String> tmpMethods = new ArrayList<>();
            if (methods == null) {
                methods = methodNames;
            } else {
                Looper.from(methodNames).foreach(tmpMethods::add);
                Looper.from(methods).foreach(tmpMethods::add);
                methods = tmpMethods.toArray(new String[tmpMethods.size()]);
            }
            tempInitModel.put(VAR_METHODS, methods);

            List<MethodMeta.Builder> methodBuilders = classBuilder.findMethodBuilders(METHOD_INIT_NAME);
            if (methodBuilders.size() > 0) {
                MethodMeta.Builder mbuilder = Looper.from(methodBuilders)
                        .filter(builder -> builder.getReturnTypeName().equals(Type.VOID))
                        .filter(builder -> builder.getParameterCount() == 0)
                        .first();
                if (mbuilder != null) {
                    return;
                }
            }

            Template tempInit = builderContext.loadTemplate(TEMP_INIT);
            classBuilder
                    .addImplement(IInitial.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                        .addModifier(Modifier.PUBLIC)
                        .setName(METHOD_INIT_NAME)
                        .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                        .addCodeBuilder(CodeMeta.builder()
                                .setTemplate(tempInit)
                                .setModel(classBuilder.getTransience(MODEL_INIT)))
                        .setReturnTypeName(Type.VOID));
        }
    }
}
