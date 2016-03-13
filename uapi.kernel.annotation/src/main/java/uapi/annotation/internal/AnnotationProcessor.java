package uapi.annotation.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import rx.Observable;
import uapi.annotation.AnnotationHandler;
import uapi.annotation.ClassMeta;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private static final String PATH_ANNOTATION_HANDLER =
            "META-INF/services/" + AnnotationHandler.class.getCanonicalName();
    private static final String TEMP_FILE = "template/generated_source.ftl";

    protected LogSupport _logger;
    private ProcessingEnvironment _procEnv;
    private Map<String, List<AnnotationHandler>> _handlers;
    private List<String> _orderedAnnotations;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        this._procEnv = processingEnv;
        this._logger = new LogSupport(processingEnv);
        this._handlers = new HashMap<>();
        initForHandler(new NotNullHandler());
        loadExternalHandler();
    }

    private void loadExternalHandler() {
        InputStream is = null;
        Scanner scanner = null;
        AnnotationOrder annoOrder = new AnnotationOrder(this._logger);

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
                    if (!(handler instanceof AnnotationHandler)) {
                        this._logger.error(
                                "The handler [{}] is not an instance of AnnotationHandler",
                                handler.getClass().getName());
                        return;
                    }
                    initForHandler((AnnotationHandler) handler);
                    annoOrder.doOrder((AnnotationHandler) handler);
                }
            }
            this._orderedAnnotations = annoOrder.getOrderedAnnotations();
            this._logger.info(this._orderedAnnotations.toString());
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

    private void initForHandler(AnnotationHandler handler) {
        handler.setLogger(this._logger);
        String handlerName = handler.getSupportAnnotationType().getCanonicalName();
        List<AnnotationHandler> handlers = this._handlers.get(handlerName);
        if (handlers == null) {
            handlers = new ArrayList<>();
            this._handlers.put(handlerName, handlers);
        }
        handlers.add(handler);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
//        this._logger.info(this._handlers.keySet().toString());
        return this._handlers.keySet();
    }

    @Override
    public boolean process(
            final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() || annotations.size() == 0) {
            return false;
        }
        this._logger.info("Start processing annotation for {} " + roundEnv.getRootElements());
        BuilderContext buildCtx = new BuilderContext(this._procEnv, roundEnv);
        // we need apply handle in order
        Observable.from(this._orderedAnnotations)
//                .filter(annoName -> {
//                    for (TypeElement annoElem : annotations) {
//                        if (annoElem.getQualifiedName().toString().equals(annoName)) {
//                            return true;
//                        }
//                    }
//                    return false;
//                })
                .flatMap(annoName -> Observable.from(_handlers.get(annoName)))
                .doOnNext(handler -> _logger.info("Invoke annotation handler -> {}", handler))
                .subscribe(handler -> handler.handle(buildCtx), _logger::error);

//        Observable.from(annotations.stream()
//                .map(annotation -> annotation.getQualifiedName().toString())
//                .collect(Collectors.toList()))
//            .flatMap(annoName -> Observable.from(_handlers.get(annoName)))
//            .subscribe(handler -> handler.handle(buildCtx), _logger::error);

        // Generate source
        this._logger.info("Starting generate source");
        generateSource(buildCtx);
        buildCtx.clearBuilders();

        this._logger.info("End processing");
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
                this._logger.info("Generate source for -> {}", classBuilder);
                ClassMeta classMeta = classBuilder.build();
                JavaFileObject fileObj = builderContext.getFiler().createSourceFile(
                        classMeta.getGeneratedClassName()
                );
                srcWriter = fileObj.openWriter();
                temp.process(classMeta, srcWriter);
                this._logger.info("Generate source for " + classMeta.getClassName());
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
