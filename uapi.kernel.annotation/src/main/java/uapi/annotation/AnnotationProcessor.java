package uapi.annotation;

import com.google.auto.service.AutoService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import uapi.annotation.handler.NotNullHandler;
import uapi.helper.StringHelper;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private Map<String, List<AnnotationHandler>> _processors;
    private ProcessingEnvironment _procEnv;
    protected LogSupport _logger;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        this._procEnv = processingEnv;
        this._logger = new LogSupport(processingEnv);
        this._processors = new HashMap<>();
        initForHandler(new NotNullHandler());
        loadExternalHandler();
    }

    private void loadExternalHandler() {
        FileObject fObj;
        InputStream is = null;
        Scanner scanner = null;
        try {
            fObj = this._procEnv.getFiler().getResource(
                    StandardLocation.CLASS_PATH,
                    StringHelper.EMPTY,
                    AnnotationHandler.class.getCanonicalName());
        } catch (IOException ex) {
            this._logger.info("Can't found or read annotation handler file - " + AnnotationHandler.class.getCanonicalName());
            return;
        }

        try {
            is = fObj.openInputStream();
            scanner = new Scanner(is);
            while (scanner.hasNext()) {
                String handlerClassName = scanner.nextLine();
                this._logger.info("Initial external annotation handler - " + handlerClassName);
                Class handlerClass = Class.forName(handlerClassName);
                Object handler = handlerClass.newInstance();
                if (! (handler instanceof AnnotationHandler)) {
                    this._logger.error("");
                    return;
                }
                initForHandler((AnnotationHandler) handler);
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

    private void initForHandler(AnnotationHandler handler) {
        handler.setLogger(this._logger);
        String handlerName = handler.getSupportAnnotationType().getCanonicalName();
        List<AnnotationHandler> handlers = this._processors.get(handlerName);
        if (handlers == null) {
            handlers = new ArrayList<>();
            this._processors.put(handlerName, handlers);
        }
        handlers.add(handler);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        this._logger.info(this._processors.keySet().toString());
        return this._processors.keySet();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this._logger.info("Start processing annotation: " + roundEnv.getRootElements());
        if (roundEnv.processingOver() || annotations.size() == 0) {
            return false;
        }

        try {
            BuilderContext buildCtx = new BuilderContext(this._procEnv);
            // Construct class type
            for (TypeElement annotation : annotations) {
                String annoName = annotation.getQualifiedName().toString();
                this._logger.info("Start handling annotation: " + annoName);
                List<AnnotationHandler> handlers = this._processors.get(annoName);
                if (handlers == null || handlers.size() == 0) {
                    this._logger.error("No handler for annotation - {}", annoName);
                    return false;
                }
                handlers.forEach(handler -> handler.handle(roundEnv, buildCtx));
            }
            // Generate source
            generateSource(buildCtx);
            buildCtx.clearBuilders();
        } catch (Exception ex) {
            this._logger.error(ex);
        }

        this._logger.info("End processing");
        return true;
    }

    private void generateSource(BuilderContext builderContext) {
        List<ClassMeta.Builder> classBuilders = builderContext.getBuilders();
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocalizedLookup(false);
        cfg.setTemplateLoader(new CompileTimeTemplateLoader(builderContext, StringHelper.EMPTY));
        Template temp;
        try {
            temp = cfg.getTemplate("template/generated_source.ftl");
        } catch (Exception ex) {
            this._logger.error(ex);
            return;
        }
        for (ClassMeta.Builder classBuilder : classBuilders) {
            Writer srcWriter = null;
            try {
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
