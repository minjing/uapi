package uapi.annotation;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;

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

    BuilderContext(final ProcessingEnvironment processingEnvironment) {
        ArgumentChecker.notNull(processingEnvironment, "processingEnvironment");
        this._procEnv = processingEnvironment;
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

    List<ClassMeta.Builder> getBuilders() {
        return this._clsBuilders;
    }

    void clearBuilders() {
        this._clsBuilders.clear();
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
