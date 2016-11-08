package uapi.behavior.internal;

import uapi.KernelException;
import uapi.annotation.IBuilderContext;
import uapi.behavior.annotation.EventBehavior;
import uapi.rx.Looper;
import uapi.service.annotation.Service;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.Set;

/**
 * The parser is used to parse Execution annotation
 */
class ExecutionParser {

    @SuppressWarnings("unchecked")
    void parse(
            final IBuilderContext builderCtx,
            final Set<? extends Element> elements) {
        Looper.from(elements).foreach(methodElement -> {
            if (methodElement.getKind() != ElementKind.METHOD) {
                throw new KernelException(
                        "The Execution annotation only can be applied on method",
                        methodElement.getSimpleName().toString());
            }
            // Check class annotations
            Element classElement = methodElement.getEnclosingElement();
            builderCtx.checkAnnotations(classElement, Service.class, EventBehavior.class);

            // check the annotated method must be like: Execution xxx();
            String methodName = methodElement.getSimpleName().toString();
            String returnTypeName = ((ExecutableElement) methodElement).getReturnType().toString();
        });
    }
}
