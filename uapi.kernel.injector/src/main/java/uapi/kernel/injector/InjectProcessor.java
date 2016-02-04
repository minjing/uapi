package uapi.kernel.injector;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.util.Set;

@SupportedAnnotationTypes({ "uapi.kernel.injector.Inject" })
public class InjectProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
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
        }

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

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
