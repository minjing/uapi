package uapi.injector;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import freemarker.template.Configuration;
import freemarker.template.Template;
import uapi.annotation.AnnotationProcessor;
import uapi.annotation.CompileTimeTemplateLoader;
import uapi.annotation.FieldMeta;
import uapi.annotation.ClassMeta;
import uapi.helper.StringHelper;
import uapi.injector.helper.ServiceHelper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//@AutoService(Processor.class)
public class InjectProcessor extends AnnotationProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Inject.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("Start processing Inject annotation");

        Set<? extends Element> fieldElmts = roundEnv.getElementsAnnotatedWith(Inject.class);
        Map<String, ClassMeta.Builder> svcBuilders = parseServices(fieldElmts);
        generateService(svcBuilders);

        log("End processing");
        return true;
    }

    private void generateService(Map<String, ClassMeta.Builder> serviceBuilders) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocalizedLookup(false);
        cfg.setTemplateLoader(new CompileTimeTemplateLoader(processingEnv, StringHelper.EMPTY));
        Template temp;
        try {
            temp = cfg.getTemplate("injectable.ftl");
        } catch (Exception ex) {
            error(ex);
            return;
        }
        for (Map.Entry<String, ClassMeta.Builder> svcBuilderEntry : serviceBuilders.entrySet()) {
            Writer srcWriter = null;
            try {
                ClassMeta svcMeta = svcBuilderEntry.getValue().build();
                JavaFileObject fileObj = processingEnv.getFiler().createSourceFile(
                        svcMeta.getGeneratedClassName()
                );
                srcWriter = fileObj.openWriter();
                temp.process(svcMeta, srcWriter);
            } catch (Exception ex) {
                error(ex);
                return;
            } finally {
                if (srcWriter != null) {
                    try {
                        srcWriter.close();
                    } catch (Exception ex) {
                        error(ex);
                    }
                }
            }
        }
    }

    private Map<String, ClassMeta.Builder> parseServices(Set<? extends Element> fieldElmts) {
        Elements elemtUtil = this.processingEnv.getElementUtils();
        Map<String, ClassMeta.Builder> svcBuilders = new HashMap<>();
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
                String key = pkgElem.getSimpleName().toString() + "." + svcClassName;
                ClassMeta.Builder builder = svcBuilders.get(key);
                if (builder == null) {
                    builder = ClassMeta.builder()
                            .setPackageName(pkgElem.getQualifiedName().toString())
                            .setClassName(classElement.getSimpleName().toString())
                            .setGeneratedClassName(ServiceHelper.generateServiceName(svcClassName));
                    svcBuilders.put(key, builder);
                }
                String fieldType = fieldElmt.asType().toString();
                String injectSvcId = fieldType;
                Inject injectAnno = fieldElmt.getAnnotation(Inject.class);
                if (!Strings.isNullOrEmpty(injectAnno.value())) {
                    injectSvcId = injectAnno.value();
                }
                builder.addProperty(FieldMeta.builder()
                        .setName(fieldElmt.getSimpleName().toString())
                        .setTypeName(fieldType)
                        .setInjectServiceId(injectSvcId)
                        .build());

                log(builder.toString());
            }
        } catch (Exception ex) {
            error(ex);
        }
        return svcBuilders;
    }
}
