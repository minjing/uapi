package uapi.web.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import rx.Observable;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ArgumentChecker;
import uapi.web.ArgumentMapping;
import uapi.web.HttpMethod;
import uapi.web.IRestfulService;
import uapi.web.annotation.FromHeader;
import uapi.web.annotation.FromParam;
import uapi.web.annotation.FromUri;
import uapi.web.annotation.Restful;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * A annotation handler for Restful annotation handling
 */
@AutoService(IAnnotationsHandler.class)
public class RestfulHandler extends AnnotationsHandler {

    private static final String TEMPLATE_GET_METHOD_ARGUMENTS_INFO = "template/getMethodArgumentsInfo_method.ftl";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations =
            new Class[] { Restful.class };

    @Override
    protected Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    protected void handleAnnotatedElements(
            final IBuilderContext builderCtx,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException {
        ArgumentChecker.notNull(annotationType, "annotationType");

        elements.forEach(methodElement -> {
            if (methodElement.getKind() != ElementKind.METHOD) {
                throw new KernelException(
                        "The Restful annotation only can be applied on field",
                        methodElement.getSimpleName().toString());
            }

            builderCtx.checkModifiers(methodElement, Restful.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            Element classElemt = methodElement.getEnclosingElement();
            builderCtx.checkModifiers(classElemt, Restful.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            String methodName = methodElement.getSimpleName().toString();
            Restful restful = methodElement.getAnnotation(Restful.class);
            HttpMethod[] httpMethods = HttpMethod.parse(restful.value());
            ExecutableElement execElem = (ExecutableElement) methodElement;
            List<ArgumentMapping> argMappings = new ArrayList<>();
            Observable.from(execElem.getParameters())
                    .subscribe(paramElem -> argMappings.add(handleFromAnnotation(paramElem)));

            Map<HttpMethod, List<ArgumentMapping>> httpMethodMappings = new HashMap<>();
            Observable.from(httpMethods)
                    .subscribe(httpMethod -> httpMethodMappings.put(httpMethod, argMappings));
            Map<String, Object> modelGetArgs = new HashMap<>();
            modelGetArgs.put("mappedArgMappings", httpMethodMappings);
            Template tempGetArgs = builderCtx.loadTemplate(TEMPLATE_GET_METHOD_ARGUMENTS_INFO);

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);
            clsBuilder.addImplement(IRestfulService.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                            .addModifier(Modifier.PUBLIC)
                            .setName("getMethodArgumentsInfo")
                            .setReturnTypeName("uapi.web.ArgumentMapping[]")
                            .addParameterBuilder(ParameterMeta.builder()
                                    .setName("method")
                                    .setType(HttpMethod.class.getCanonicalName()))
                            .addCodeBuilder(CodeMeta.builder()
                                    .setModel(modelGetArgs)
                                    .setTemplate(tempGetArgs)));
        });
    }

    private ArgumentMapping handleFromAnnotation(
            final Element paramElem
    ) {
        checkParamAnnotation(paramElem);
        String paramType = paramElem.asType().toString();
        FromUri fromUri = paramElem.getAnnotation(FromUri.class);
        FromHeader fromHeader = paramElem.getAnnotation(FromHeader.class);
        FromParam fromParam = paramElem.getAnnotation(FromParam.class);
        ArgumentMapping argMapping = null;
        if (fromUri != null) {
            argMapping = new ArgumentMapping(ArgumentMapping.From.Uri, paramType);
        } else if (fromHeader != null) {
            argMapping = new ArgumentMapping(ArgumentMapping.From.Header, paramType);
        } else if (fromParam != null) {
            argMapping = new ArgumentMapping(ArgumentMapping.From.Param, paramType);
        }
        return argMapping;
    }

    private void checkParamAnnotation(Element paramElem) {
        FromUri fromUri = paramElem.getAnnotation(FromUri.class);
        FromHeader fromHeader = paramElem.getAnnotation(FromHeader.class);
        FromParam fromParam = paramElem.getAnnotation(FromParam.class);
        boolean valid = true;
        if (fromUri != null) {
            if (fromHeader != null || fromParam != null) {
                valid = false;
            }
        }
        if (fromHeader != null) {
            if (fromUri != null || fromParam != null) {
                valid = false;
            }
        }
        if (fromParam != null) {
            if (fromUri != null || fromHeader != null) {
                valid = false;
            }
        }
        if (! valid) {
            throw new KernelException(
                    "The restful method parameter {} allow only one FromUri/FromHeader/FromParam Annotation",
                    paramElem.getSimpleName().toString());
        }
        if (fromUri == null && fromHeader == null && fromParam == null) {
            throw new KernelException(
                    "The restful method parameter {} has to define one of FromUri/FromHeader/FromParam Annotation",
                    paramElem.getSimpleName().toString());
        }
    }
}
