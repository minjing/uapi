package uapi.annotation;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private Map<String, List<AnnotationHandler>> _processors;
    protected LogSupport _logger;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        this._processors = new HashMap<>();
        this._logger = new LogSupport(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                NotNullHandler.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this._logger.info("Start processing annotation");

        try {
            // Construct class type
            BuilderContext buildCtx = new BuilderContext(this.processingEnv);
            for (TypeElement annotation : annotations) {
                String annoName = annotation.getSimpleName().toString();
                List<AnnotationHandler> handlers = this._processors.get(annoName);
                if (handlers == null || handlers.size() == 0) {
                    this._logger.error("No handler for annotation {}", annoName);
                    return true;
                }
                handlers.forEach(handler -> handler.handle(roundEnv, buildCtx));
            }
            // Generate source
            // TODO:
        } catch (Exception ex) {
            this._logger.error(ex);
        }

        this._logger.info("End processing");
        return true;
    }
}
