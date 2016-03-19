package uapi.service.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.service.SetterMeta;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.*;

/**
 * The handler is used to handle Optional annotation
 */
@AutoService(AnnotationHandler.class)
public class OptionalHandler extends AnnotationHandler<Optional> {

    private static final String TEMPLATE_IS_OPTIONAL = "template/isOptional_method.ftl";

    @Override
    public Class<Optional> getSupportAnnotationType() {
        return Optional.class;
    }

    @Override
    public Class[] afterHandledAnnotations() {
        return new Class[] { Inject.class };
    }

    @Override
    public void handle(
            final IBuilderContext builderCtx
    ) throws KernelException {
        Set<? extends Element> elements = builderCtx.getElementsAnnotatedWith(Optional.class);
//        if (elements.size() == 0) {
//            return;
//        }

        getLogger().info("Starting process Option annotation");
        // Initialize optional setters
        elements.forEach(fieldElement -> {
            if (fieldElement.getKind() != ElementKind.FIELD) {
                throw new KernelException(
                        "The Optional annotation only can be applied on field",
                        fieldElement.getSimpleName().toString());
            }
            checkModifiers(fieldElement, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            Element classElemt = fieldElement.getEnclosingElement();
            checkModifiers(classElemt, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            String fieldName = fieldElement.getSimpleName().toString();

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            clsBuilder.findSetterBuilders().stream()
                    .map(setter -> (SetterMeta.Builder) setter)
                    .filter(setter -> setter.getFieldName().equals(fieldName))
                    .forEach(setter -> setter.setIsOptional(true));
        });

        // implement isOptional method
        String methodName       = "isOptional";
        String methodReturnType = Type.BOOLEAN;
        String paramName        = "id";
        String paramType        = Type.STRING;

        builderCtx.getBuilders().forEach(classBuilder -> {
            List<MethodMeta.Builder> setters = classBuilder.findSetterBuilders();
            if (setters.size() == 0) {
                // No setters means this class does not implement IInjectable interface
                return;
            }
            final List<String> optionals = new ArrayList<>();
            setters.stream()
                    .map(setter -> (SetterMeta.Builder) setter)
                    .filter(SetterMeta.Builder::getIsOptional)
                    .forEach(setter -> optionals.add(setter.getInjectId()));

            final Template temp = builderCtx.loadTemplate(TEMPLATE_IS_OPTIONAL);
            final Map<String, List<String>> tempModel = new HashMap<>();
            tempModel.put("optionals", optionals);

            getLogger().info("Generate isOptional for {}", classBuilder.getClassName());
            classBuilder.addMethodBuilder(MethodMeta.builder()
                    .addAnnotationBuilder(AnnotationMeta.builder().setName("Override"))
                    .setName(methodName)
                    .setReturnTypeName(methodReturnType)
                    .addModifier(Modifier.PUBLIC)
                    .addThrowTypeName(InvalidArgumentException.class.getName())
                    .addParameterBuilder(ParameterMeta.builder()
                            .setName(paramName)
                            .setType(paramType))
                    .addCodeBuilder(CodeMeta.builder()
                            .setTemplate(temp)
                            .setModel(tempModel)));
        });
    }
}
