package uapi.kernel.injector;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.util.Set;

@SupportedAnnotationTypes({ "uapi.kernel.injector.Inject" })
public class InjectProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " + e.toString());

                if (e.getKind() == ElementKind.CLASS) {
                    TypeElement clsElement = (TypeElement) e;
                    PackageElement pkgElement = (PackageElement) clsElement.getEnclosingElement();

                    try {
                        JavaFileObject jFileObj = processingEnv.getFiler().createSourceFile(
                                clsElement.getQualifiedName() + "Info");

                        BufferedWriter writer = new BufferedWriter(jFileObj.openWriter());
                        writer.append("package ").append(pkgElement.getQualifiedName()).append(";");
                        writer.newLine();
                        writer.newLine();
                        writer.append("public class ").append(clsElement.getSimpleName() + "Info").append(" { }");
                        writer.newLine();
                        writer.flush();
                        writer.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
