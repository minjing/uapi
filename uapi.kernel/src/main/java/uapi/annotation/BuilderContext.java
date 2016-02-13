package uapi.annotation;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A context for building class builder
 */
final class BuilderContext {

    private final ProcessingEnvironment _procEnv;
    private final Elements _elementUtil;
    private final List<ClassMeta.Builder> _clsBuilders = new ArrayList<>();

    BuilderContext(final ProcessingEnvironment processingEnvironment) {
        this._procEnv = processingEnvironment;
        this._elementUtil = this._procEnv.getElementUtils();
    }

    ProcessingEnvironment getProcessingEnvironment() {
        return this._procEnv;
    }

    Elements getElementUtils() {
        return this._elementUtil;
    }

    ClassMeta.Builder findClassBuilder(Element classElement) {
        ArgumentChecker.notNull(classElement, "classElement");
        ClassMeta.Builder classBuilder = ClassMeta.builder(classElement, this);
//        Element pkgElem = this._procEnv.getElementUtils().getPackageOf(classElement);
//        String pkgName = pkgElem.getSimpleName().toString();
//        String className = classElement.getSimpleName().toString();
        List<ClassMeta.Builder> matchedClassBuilders = this._clsBuilders.parallelStream()
                .filter(existing -> existing.equals(classBuilder))
                .collect(Collectors.toList());
        if (matchedClassBuilders.size() != 1) {
            throw new KernelException(
                    "Expect found only 1 class builder for {}, but found {}",
                    classBuilder.getPackageName() + "." + classBuilder.getClassName(),
                    matchedClassBuilders.size());
        }
        ClassMeta.Builder clsBuilder = matchedClassBuilders.get(0);;
        this._clsBuilders.add(clsBuilder);
        return clsBuilder;
    }

    private String generateSubClassName(String superClassName) {
        return superClassName + "_Generated";
    }
}
