package uapi.annotation;

import freemarker.template.Template;
import uapi.KernelException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * A class file builder context
 */
public interface IBuilderContext {

    ProcessingEnvironment getProcessingEnvironment();

    RoundEnvironment getRoundEnvironment();

    LogSupport getLogger();

    Elements getElementUtils();

    Types getTypeUtils();

    Filer getFiler();

    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> annotationType);

    List<ClassMeta.Builder> getBuilders();

    void clearBuilders();

    Template loadTemplate(String templatePath);

    ClassMeta.Builder findClassBuilder(Element classElement);

    void checkModifiers(
            final Element element,
            final Class<? extends Annotation> annotation,
            final Modifier... unexpectedModifiers
    ) throws KernelException;

    Element findFieldWith(
            final Element classElement,
            final Class<?> fieldType,
            final Class annotationType);
}
