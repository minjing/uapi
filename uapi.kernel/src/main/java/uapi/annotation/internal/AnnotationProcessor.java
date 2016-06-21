/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.annotation.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import rx.Observable;
import uapi.annotation.AnnotationsHandler;
import uapi.annotation.ClassMeta;
import uapi.annotation.IAnnotationsHandler;
import uapi.annotation.LogSupport;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.*;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private static final String PATH_ANNOTATION_HANDLER =
            "META-INF/services/" + IAnnotationsHandler.class.getCanonicalName();
    private static final String TEMP_FILE = "template/generated_source.ftl";

    protected LogSupport _logger;
    private ProcessingEnvironment _procEnv;
    private List<IAnnotationsHandler> _handlers;
    private Set<String> _orderedAnnotations;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        this._procEnv = processingEnv;
        this._logger = new LogSupport(processingEnv);
        this._handlers = new LinkedList<>();
        this._orderedAnnotations = new HashSet<>();
        loadExternalHandler();
    }

    private void loadExternalHandler() {
        InputStream is = null;
        Scanner scanner = null;

        try {
            final Enumeration<URL> systemResources =
                    this.getClass().getClassLoader().getResources(PATH_ANNOTATION_HANDLER);
            while (systemResources.hasMoreElements()) {
                is = systemResources.nextElement().openStream();
                scanner = new Scanner(is);
                while (scanner.hasNext()) {
                    String handlerClassName = scanner.nextLine();
                    this._logger.info("Initial external annotation handler - " + handlerClassName);
                    Class handlerClass = Class.forName(handlerClassName);
                    Object handler = handlerClass.newInstance();
                    if (!(handler instanceof AnnotationsHandler)) {
                        this._logger.error(
                                "The handler [{}] is not an instance of AnnotationsHandler",
                                handler.getClass().getName());
                        return;
                    }
                    initForHandler((AnnotationsHandler) handler);
                }
            }
        } catch (Exception ex) {
            this._logger.error(ex);
            return;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    this._logger.error(ex);
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private void initForHandler(IAnnotationsHandler handler) {
        this._handlers.add(handler);
        Observable.from(handler.getSupportedAnnotations())
                .map(Class::getName)
                .subscribe(_orderedAnnotations::add);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return this._orderedAnnotations;
    }

    @Override
    public boolean process(
            final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() || annotations.size() == 0) {
            return false;
        }
        //this._logger.info("Start processing annotation for {} " + roundEnv.getRootElements());
        BuilderContext buildCtx = new BuilderContext(this._procEnv, roundEnv);
        // Init for builder context
        Observable.from(this._handlers)
                .map(handler -> handler.getHelper())
                .filter(helper -> helper != null)
                .subscribe(buildCtx::putHelper);
        Observable.from(this._handlers)
                .doOnNext(handler -> _logger.info("Invoke annotation handler -> {}", handler))
                .subscribe(handler -> handler.handle(buildCtx), _logger::error);

        // Generate source
        //this._logger.info("Starting generate source");
        generateSource(buildCtx);
        buildCtx.clearBuilders();

        //this._logger.info("End processing");
        return true;
    }

    private void generateSource(BuilderContext builderContext) {
        List<ClassMeta.Builder> classBuilders = builderContext.getBuilders();

        Template temp;
        try {
            temp = builderContext.loadTemplate(TEMP_FILE);
        } catch (Exception ex) {
            this._logger.error(ex);
            return;
        }

        for (ClassMeta.Builder classBuilder : classBuilders) {
            Writer srcWriter = null;
            try {
                //this._logger.info("Generate source for -> {}", classBuilder);
                ClassMeta classMeta = classBuilder.build();
                JavaFileObject fileObj = builderContext.getFiler().createSourceFile(
                        classMeta.getGeneratedClassName()
                );
                srcWriter = fileObj.openWriter();
                temp.process(classMeta, srcWriter);
                //this._logger.info("Generate source for " + classMeta.getClassName());
            } catch (Exception ex) {
                this._logger.error(ex);
                return;
            } finally {
                if (srcWriter != null) {
                    try {
                        srcWriter.close();
                    } catch (Exception ex) {
                        this._logger.error(ex);
                    }
                }
            }
        }
    }
}
