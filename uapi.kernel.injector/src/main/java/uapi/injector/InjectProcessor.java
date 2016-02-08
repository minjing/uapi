package uapi.injector;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import uapi.AnnotationProcessor;
import uapi.injector.helper.ServiceHelper;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
public class InjectProcessor extends AnnotationProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Inject.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("Start processing Inject annotation");

        Set<? extends Element> fieldElmts = roundEnv.getElementsAnnotatedWith(Inject.class);
        Map<String, ServiceMeta.Builder> svcBuilders = parseServices(fieldElmts);
        generateService(svcBuilders);

        log("End processing");
        return true;
    }

    private void generateService(Map<String, ServiceMeta.Builder> serviceBuilders) {
        serviceBuilders.forEach((qualifiedSvcName, svcBuilder) -> {

        });
    }

    private Map<String, ServiceMeta.Builder> parseServices(Set<? extends Element> fieldElmts) {
        Elements elemtUtil = this.processingEnv.getElementUtils();
        Map<String, ServiceMeta.Builder> svcBuilders = new HashMap<>();
        try {
            for (Element fieldElmt : fieldElmts) {
                Set<Modifier> elmtModifiers = fieldElmt.getModifiers();
                if (elmtModifiers.contains(Modifier.STATIC) ||
                        elmtModifiers.contains(Modifier.FINAL) ||
                        elmtModifiers.contains(Modifier.PRIVATE)) {
                    throw new IllegalStateException(
                            "Field with Inject annotation must not be private, static or final");
                }
                Element classElement = fieldElmt.getEnclosingElement();
                Set<Modifier> classMeodifiers = classElement.getModifiers();
                if (classMeodifiers.contains(Modifier.STATIC) ||
                        classMeodifiers.contains(Modifier.FINAL) ||
                        classMeodifiers.contains(Modifier.PRIVATE)) {
                    throw new IllegalStateException(
                            "Class with Inject annotation field must not be private, static or final");
                }

                PackageElement pkgElem = elemtUtil.getPackageOf(fieldElmt);
                String svcClassName = classElement.getSimpleName().toString();
                ServiceMeta.Builder builder = svcBuilders.get(svcClassName);
                if (builder == null) {
                    builder = ServiceMeta.builder()
                            .setServicePackageName(pkgElem.getQualifiedName().toString())
                            .setServiceClassName(classElement.getSimpleName().toString())
                            .setGeneratedClassName(ServiceHelper.generateServiceName(svcClassName));
                }
                String fieldType = fieldElmt.asType().toString();
                String injectSvcId = fieldType;
                Inject injectAnno = fieldElmt.getAnnotation(Inject.class);
                if (!Strings.isNullOrEmpty(injectAnno.value())) {
                    injectSvcId = injectAnno.value();
                }
                builder.addFieldMeta(FieldMeta.builder()
                        .setFieldName(fieldElmt.getSimpleName().toString())
                        .setFieldTypeName(fieldType)
                        .setInjectServiceId(injectSvcId)
                        .build());

                log(builder.toString());
                svcBuilders.put(builder.getGeneratedClassName(), builder);
            }
        } catch (Exception ex) {
            error(ex);
        }
        return svcBuilders;
    }
}
