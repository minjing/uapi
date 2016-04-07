package uapi.config.internal;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import freemarker.template.Template;
import rx.Observable;
import uapi.KernelException;
import uapi.annotation.*;
import uapi.config.IConfigValueParser;
import uapi.config.IConfigurable;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * The handler is used to handle Config related annotatioin
 */
@AutoService(IAnnotationsHandler.class)
public class ConfigHandler extends AnnotationsHandler {

    private static final String CONFIG_INFOS                = "ConfigInfos";
    private static final String FIELD_SVC_REG               = "FieldServiceRegistry";

    private static final String TEMPLATE_GET_PATHS          = "template/getPaths_method.ftl";
    private static final String TEMPLATE_IS_OPTIONAL_CONFIG = "template/isOptionalConfig_method.ftl";
    private static final String TEMPLATE_CONFIG             = "template/config_method.ftl";

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] orderedAnnotations = new Class[] { Config.class };

    @Override
    protected Class<? extends Annotation>[] getOrderedAnnotations() {
        return orderedAnnotations;
    }

    @Override
    protected void handleAnnotatedElements(
            final IBuilderContext builderContext,
            final Class<? extends Annotation> annotationType,
            final Set<? extends Element> elements
    ) throws KernelException {
        elements.forEach(fieldElement -> {
            if (fieldElement.getKind() != ElementKind.FIELD) {
                throw new KernelException(
                        "The Config annotation only can be applied on field",
                        fieldElement.getSimpleName().toString());
            }
            builderContext.checkModifiers(fieldElement, Config.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

            Element classElement = fieldElement.getEnclosingElement();
            checkModifiers(classElement, Config.class, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            checkAnnotations(classElement, Service.class);

            // Get field which is reference IRegistry instance
            Element svcRegElem = builderContext.findFieldWith(classElement, IRegistry.class, Inject.class);
            if (svcRegElem == null) {
                throw new KernelException(
                        "The {} must defined an field with type {} and annotated with {}",
                        classElement, IRegistry.class.getName(), Inject.class.getName());
            }
            String svcRegFieldName = svcRegElem.getSimpleName().toString();

            ClassMeta.Builder classBuilder = builderContext.findClassBuilder(classElement);
            classBuilder.putTransience(FIELD_SVC_REG, svcRegFieldName);
            List<ConfigInfo> cfgInfos = classBuilder.getTransience(CONFIG_INFOS);
            if (cfgInfos == null) {
                cfgInfos = new ArrayList<>();
                classBuilder.putTransience(CONFIG_INFOS, cfgInfos);
            }
            ConfigInfo cfgInfo = new ConfigInfo();
            Config cfg = fieldElement.getAnnotation(Config.class);
            AnnotationMirror cfgMirror = MoreElements.getAnnotationMirror(fieldElement, Config.class).get();
            String parserType = getTypeInAnnotation(cfgMirror, "parser", builderContext.getLogger());
            // The parser is set to IConfigValueParser.class means no customized parser was defined
            if (! IConfigValueParser.class.getCanonicalName().equals(parserType)) {
                cfgInfo.parserName = parserType;
            }
            cfgInfo.path = cfg.path();
            cfgInfo.optional = cfg.optional();
            cfgInfo.fieldName = fieldElement.getSimpleName().toString();
            cfgInfo.fieldType = fieldElement.asType().toString();
            cfgInfos.add(cfgInfo);
        });

        Template tempGetPaths = builderContext.loadTemplate(TEMPLATE_GET_PATHS);
        Template tempIsOptionalConfig = builderContext.loadTemplate(TEMPLATE_IS_OPTIONAL_CONFIG);
        Template tempConfig = builderContext.loadTemplate(TEMPLATE_CONFIG);

        Observable.from(builderContext.getBuilders()).subscribe(classBuilder -> {
            List<ConfigInfo> configInfos = classBuilder.getTransience(CONFIG_INFOS);
            String fieldSvcReg = classBuilder.getTransience(FIELD_SVC_REG);
            if (configInfos == null) {
                return;
            }
            Map<String, Object> tempModel = new HashMap<>();
            tempModel.put("configInfos", configInfos);
            tempModel.put("fieldSvcReg", fieldSvcReg);
            classBuilder
                    .addImplement(IConfigurable.class.getCanonicalName())
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                            .addModifier(Modifier.PUBLIC)
                            .setName(IConfigurable.METHOD_GET_PATHS)
                            .setReturnTypeName(Type.STRING_ARRAY)
                            .addCodeBuilder(CodeMeta.builder()
                                    .setModel(tempModel)
                                    .setTemplate(tempGetPaths)))
                    .addMethodBuilder(MethodMeta.builder()
                            .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                            .addModifier(Modifier.PUBLIC)
                            .setName(IConfigurable.METHOD_IS_OPTIONAL_CONFIG)
                            .addParameterBuilder(ParameterMeta.builder()
                                    .setName(IConfigurable.PARAM_PATH)
                                    .setType(Type.STRING))
                            .setReturnTypeName(Type.BOOLEAN)
                            .addCodeBuilder(CodeMeta.builder()
                                    .setModel(tempModel)
                                    .setTemplate(tempIsOptionalConfig)))
                    .addMethodBuilder(MethodMeta.builder()
                                    .addAnnotationBuilder(AnnotationMeta.builder().setName(AnnotationMeta.OVERRIDE))
                                    .addModifier(Modifier.PUBLIC)
                                    .setName(IConfigurable.METHOD_CONFIG)
                                    .addParameterBuilder(ParameterMeta.builder()
                                            .setName(IConfigurable.PARAM_PATH)
                                            .setType(Type.STRING))
                                    .addParameterBuilder(ParameterMeta.builder()
                                            .setName(IConfigurable.PARAM_CONFIG_OBJECT)
                                            .setType(Type.OBJECT))
                                    .setReturnTypeName(Type.VOID)
                                    .addCodeBuilder(CodeMeta.builder()
                                            .setModel(tempModel)
                                            .setTemplate(tempConfig))
                    );
        });
    }

    public static final class ConfigInfo {

        private String path;
        private String fieldName;
        private String fieldType;
        private boolean optional;
        private String parserName;

        public String getPath() {
            return this.path;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public String getFieldType() {
            return this.fieldType;
        }

        public boolean getOptional() {
            return this.optional;
        }

        public String getParserName() {
            return this.parserName;
        }

        public boolean hasParser() {
            return ! ArgumentChecker.isEmpty(this.parserName);
        }
    }
}
