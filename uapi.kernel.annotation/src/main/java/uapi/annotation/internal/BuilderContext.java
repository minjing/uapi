package uapi.annotation.internal;

import freemarker.template.Configuration;
import freemarker.template.Template;
import rx.Observable;
import uapi.KernelException;
import uapi.annotation.AnnotationHandler;
import uapi.annotation.ClassMeta;
import uapi.annotation.IBuilderContext;
import uapi.annotation.LogSupport;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A context for building class builder
 */
public final class BuilderContext implements IBuilderContext {

    private final LogSupport _logger;
    private final ProcessingEnvironment _procEnv;
    private final RoundEnvironment _roundEnv;
    private final List<ClassMeta.Builder> _clsBuilders = new ArrayList<>();
    private final Configuration _tempConf;

    public BuilderContext(
            final ProcessingEnvironment processingEnvironment,
            final RoundEnvironment roundEnvironment) {
        ArgumentChecker.notNull(processingEnvironment, "processingEnvironment");
        ArgumentChecker.notNull(roundEnvironment, "roundEnvironment");
        this._procEnv = processingEnvironment;
        this._roundEnv = roundEnvironment;
        this._logger = new LogSupport(processingEnvironment);
        // Initialize freemarker template configuration
        this._tempConf = new Configuration(Configuration.VERSION_2_3_22);
        this._tempConf.setDefaultEncoding("UTF-8");
        this._tempConf.setLocalizedLookup(false);
        this._tempConf.setTemplateLoader(
                new CompileTimeTemplateLoader(this, StringHelper.EMPTY));
    }

    @Override
    public ProcessingEnvironment getProcessingEnvironment() {
        return this._procEnv;
    }

    @Override
    public RoundEnvironment getRoundEnvironment() {
        return this._roundEnv;
    }

    @Override
    public LogSupport getLogger() {
        return this._logger;
    }

    @Override
    public Elements getElementUtils() {
        return this._procEnv.getElementUtils();
    }

    @Override
    public Types getTypeUtils() {
        return this._procEnv.getTypeUtils();
    }

    @Override
    public Filer getFiler() {
        return this._procEnv.getFiler();
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith (
            final Class<? extends Annotation> annotationType) {
        ArgumentChecker.notNull(annotationType, "annotationType");
        return this._roundEnv.getElementsAnnotatedWith(annotationType);
    }

    @Override
    public List<ClassMeta.Builder> getBuilders() {
        return this._clsBuilders;
    }

    @Override
    public void clearBuilders() {
        this._clsBuilders.clear();
    }

    @Override
    public Template loadTemplate(String templatePath) {
        ArgumentChecker.notEmpty(templatePath, "templatePath");
        Template temp;
        try {
            temp = this._tempConf.getTemplate(templatePath);
        } catch (Exception ex) {
            throw new KernelException(ex);
        }
        return temp;
    }

    @Override
    public ClassMeta.Builder findClassBuilder(Element classElement) {
        ArgumentChecker.notNull(classElement, "classElement");
        final ClassMeta.Builder expectedBuilder = ClassMeta.builder(classElement, this);
        List<ClassMeta.Builder> matchedClassBuilders = this._clsBuilders.parallelStream()
                .filter(existing -> existing.equals(expectedBuilder))
                .collect(Collectors.toList());
        ClassMeta.Builder clsBuilder;
        if (matchedClassBuilders.size() == 0) {
            this._clsBuilders.add(expectedBuilder);
            clsBuilder = expectedBuilder;
        } else if (matchedClassBuilders.size() == 1) {
            clsBuilder = matchedClassBuilders.get(0);
        } else {
            throw new KernelException(
                    "Expect found only 1 class builder for {}, but found {}",
                    expectedBuilder.getPackageName() + "." + expectedBuilder.getClassName(),
                    matchedClassBuilders.size());
        }
        return clsBuilder;
    }

    @Override
    public void checkModifiers(
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
    public Element findFieldWith(
            final Element classElement,
            final Class<?> fieldType,
            final Class annotationType) {
        ArgumentChecker.notNull(classElement, "classElement");
        ArgumentChecker.notNull(fieldType, "fieldType");
        ArgumentChecker.notNull(annotationType, "annotationType");
        List<Element> elems = (List<Element>) Observable.from(classElement.getEnclosedElements())
                .filter(element -> element.getKind() == ElementKind.FIELD)
                .filter(fieldElement -> fieldElement.asType().toString().equals(fieldType.getCanonicalName()))
                .filter(fieldElement -> fieldElement.getAnnotation(annotationType) != null)
                .toList().toBlocking().single();
        if (elems == null || elems.size() == 0) {
            return null;
        }
        return elems.get(0);
    }
}
