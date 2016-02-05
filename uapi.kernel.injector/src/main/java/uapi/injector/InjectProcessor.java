package uapi.injector;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import uapi.AnnotationProcessor;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
public class InjectProcessor extends AnnotationProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Inject.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("start");
        Messager messager = processingEnv.getMessager();
        Set<? extends Element> elmts = roundEnv.getElementsAnnotatedWith(Inject.class);
        for (Element elmt : elmts) {
            Set<Modifier> elmtModifiers = elmt.getModifiers();
            if (elmtModifiers.contains(Modifier.STATIC) ||
                    elmtModifiers.contains(Modifier.FINAL) ||
                    elmtModifiers.contains(Modifier.PRIVATE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Field must not be private, static or final");
            }

            Element parentClass = elmt.getEnclosingElement();
            Name fieldName = elmt.getSimpleName();
            TypeMirror fieldType = elmt.asType();

            Setter setter = new Setter();
            setter.setClassName(parentClass.getSimpleName() + "_Bean");
            setter.setSuperClassName(parentClass.getSimpleName().toString());
            setter.setFieldName(fieldName.toString());
            setter.setFieldTypeName(fieldType.toString());
            log(setter.toString());
        }
        log("end");

//        for (TypeElement te : annotations) {
//            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
//                messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " + e.toString());
//
//                if (e.getKind() == ElementKind.CLASS) {
//                    TypeElement clsElement = (TypeElement) e;
//                    PackageElement pkgElement = (PackageElement) clsElement.getEnclosingElement();
//
//                    try {
//                        JavaFileObject jFileObj = processingEnv.getFiler().createSourceFile(
//                                clsElement.getQualifiedName() + "Info");
//
//                        BufferedWriter writer = new BufferedWriter(jFileObj.openWriter());
//                        writer.append("package ").append(pkgElement.getQualifiedName()).append(";");
//                        writer.newLine();
//                        writer.newLine();
//                        writer.append("public class ").append(clsElement.getSimpleName() + "Info").append(" { }");
//                        writer.newLine();
//                        writer.flush();
//                        writer.close();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        }

        return true;
    }
}
