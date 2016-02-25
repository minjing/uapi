package uapi.annotation;

import freemarker.template.Template;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * Created by min on 16/2/25.
 */
public interface IBuilderContext {

    ProcessingEnvironment getProcessingEnvironment();

    Elements getElementUtils();

    Types getTypeUtils();

    Filer getFiler();

    List<ClassMeta.Builder> getBuilders();

    void clearBuilders();

    Template loadTemplate(String templatePath);

    ClassMeta.Builder findClassBuilder(Element classElement);
}
