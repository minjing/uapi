package uapi.web.internal;

import com.google.auto.service.AutoService;
import freemarker.template.Template;
import rx.Observable;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.helper.ArgumentChecker;
import uapi.helper.MapHelper;
import uapi.helper.StringHelper;
import uapi.service.annotation.Exposure;
import uapi.service.annotation.Service;
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

    private static final String TEMPLATE_GET_METHOD_ARGUMENTS_INFO  = "template/getMethodArgumentsInfo_method.ftl";
    private static final String TEMPLATE_INVOKE                     = "template/invoke_method.ftl";
    private static final String HTTP_TO_METHOD_ARGS_MAPPING         = "HttpToMethodArgumentsMapping";
    private static final String EXPOSED_NAME                        = "ExposedName";

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

        Observable.from(elements).subscribe(methodElement -> {
            if (methodElement.getKind() != ElementKind.METHOD) {
                throw new KernelException(
                        "The Restful annotation only can be applied on field",
                        methodElement.getSimpleName().toString());
            }
            Element classElement = methodElement.getEnclosingElement();
            checkAnnotations(classElement, Service.class);

            builderCtx.checkModifiers(methodElement, Restful.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            Element classElemt = methodElement.getEnclosingElement();
            builderCtx.checkModifiers(classElemt, Restful.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            ClassMeta.Builder clsBuilder = builderCtx.findClassBuilder(classElemt);

            Exposure exposure = classElement.getAnnotation(Exposure.class);
            String exposedName = exposure == null ? classElement.getSimpleName().toString() : exposure.value();
            ArgumentChecker.notEmpty(exposedName, "exposedName");
            clsBuilder.putTransience(EXPOSED_NAME, exposedName);

            String methodName = methodElement.getSimpleName().toString();
            Restful restful = methodElement.getAnnotation(Restful.class);
            HttpMethod[] httpMethods = HttpMethod.parse(restful.value());

            Map<String, MethodArgumentsMapping> httpMethodArgMappings =
                    clsBuilder.createTransienceIfAbsent(HTTP_TO_METHOD_ARGS_MAPPING, HashMap::new);
            HttpMethod found = MapHelper.findKey(httpMethodArgMappings, httpMethods);
            if (found != null) {
                throw new KernelException("Found multiple methods are mapped to same http method: {}", found);
            }


            ExecutableElement execElem = (ExecutableElement) methodElement;
            MethodArgumentsMapping methodArgMapping = new MethodArgumentsMapping(methodName);
            Observable.from(execElem.getParameters())
                    .map(this::handleFromAnnotation)
                    .subscribe(methodArgMapping::addArgumentMapping);
            Observable.from(httpMethods)
                    .subscribe(httpMethod -> httpMethodArgMappings.put(httpMethod.toString(), methodArgMapping));
        }, t -> builderCtx.getLogger().error(t));

        implementIRestfulService(builderCtx);
    }

    private void implementIRestfulService(
            final IBuilderContext builderCtx
    ) {
        Template tempGetArgs = builderCtx.loadTemplate(TEMPLATE_GET_METHOD_ARGUMENTS_INFO);
        Template tempInvoke = builderCtx.loadTemplate(TEMPLATE_INVOKE);

        Observable.from(builderCtx.getBuilders())
                .filter(clsBuilder -> clsBuilder.getTransience(HTTP_TO_METHOD_ARGS_MAPPING) != null)
                .subscribe(clsBuilder -> {
                    Map<HttpMethod, MethodArgumentsMapping> httpMethodMappings = clsBuilder.getTransience(HTTP_TO_METHOD_ARGS_MAPPING);
                    String codeGetId = StringHelper.makeString("return \"{}\";", clsBuilder.getTransience(EXPOSED_NAME).toString());
                    Map<String, Object> model = new HashMap<>();
                    model.put("model", httpMethodMappings);
                    clsBuilder.addImplement(IRestfulService.class.getCanonicalName())
                            // implement getId method
                            .addMethodBuilder(MethodMeta.builder()
                                    .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                                    .addModifier(Modifier.PUBLIC)
                                    .setName("getId")
                                    .setReturnTypeName(String.class.getCanonicalName())
                                    .addCodeBuilder(CodeMeta.builder()
                                            .addRawCode(codeGetId)))
                            // implement getMethodArgumentsInfo method
                            .addMethodBuilder(MethodMeta.builder()
                                    .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                                    .addModifier(Modifier.PUBLIC)
                                    .setName("getMethodArgumentsInfo")
                                    .setReturnTypeName("uapi.web.ArgumentMapping[]")
                                    .addParameterBuilder(ParameterMeta.builder()
                                            .setName("method")
                                            .setType(HttpMethod.class.getCanonicalName()))
                                    .addCodeBuilder(CodeMeta.builder()
                                            .setModel(model)
                                            .setTemplate(tempGetArgs)))
                            // implement invoke method
                            .addMethodBuilder(MethodMeta.builder()
                                    .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                                    .addModifier(Modifier.PUBLIC)
                                    .setName("invoke")
                                    .setReturnTypeName(Object.class.getCanonicalName())
                                    .addParameterBuilder(ParameterMeta.builder()
                                            .setName("method")
                                            .setType(HttpMethod.class.getCanonicalName()))
                                    .addParameterBuilder(ParameterMeta.builder()
                                            .setName("args")
                                            .setType("java.util.List<Object>"))
                                    .addCodeBuilder(CodeMeta.builder()
                                            .setModel(model)
                                            .setTemplate(tempInvoke)));
                }, t -> builderCtx.getLogger().error(t));
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
