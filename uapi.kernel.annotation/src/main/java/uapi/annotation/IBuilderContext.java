package uapi.annotation;

import freemarker.template.Template;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * Created by min on 16/2/25.
 */
public interface IBuilderContext {

    ProcessingEnvironment getProcessingEnvironment();

    RoundEnvironment getRoundEnvironment();

    Elements getElementUtils();

    Types getTypeUtils();

    Filer getFiler();

    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> annotationType);

    List<ClassMeta.Builder> getBuilders();

    void clearBuilders();

    Template loadTemplate(String templatePath);

    ClassMeta.Builder findClassBuilder(Element classElement);
}
