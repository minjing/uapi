package uapi.annotation;

import freemarker.template.Configuration;
import freemarker.template.Template;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A context for building class builder
 */
public final class BuilderContext {

    private final ProcessingEnvironment _procEnv;
    private final List<ClassMeta.Builder> _clsBuilders = new ArrayList<>();
    private final Configuration _tempConf;

    BuilderContext(final ProcessingEnvironment processingEnvironment) {
        ArgumentChecker.notNull(processingEnvironment, "processingEnvironment");
        this._procEnv = processingEnvironment;
        // Initialize freemarker template configuration
        this._tempConf = new Configuration(Configuration.VERSION_2_3_22);
        this._tempConf.setDefaultEncoding("UTF-8");
        this._tempConf.setLocalizedLookup(false);
        this._tempConf.setTemplateLoader(
                new CompileTimeTemplateLoader(this, StringHelper.EMPTY));
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return this._procEnv;
    }

    public Elements getElementUtils() {
        return this._procEnv.getElementUtils();
    }

    public Types getTypeUtils() {
        return this._procEnv.getTypeUtils();
    }

    Filer getFiler() {
        return this._procEnv.getFiler();
    }

    public List<ClassMeta.Builder> getBuilders() {
        return this._clsBuilders;
    }

    void clearBuilders() {
        this._clsBuilders.clear();
    }

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

    private String generateSubClassName(String superClassName) {
        return superClassName + "_Generated";
    }
}
