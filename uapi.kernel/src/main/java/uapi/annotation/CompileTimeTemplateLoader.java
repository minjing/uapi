package uapi.annotation;

import freemarker.cache.TemplateLoader;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Reader;

/**
 * The template loader for compiling source time
 */
public class CompileTimeTemplateLoader implements TemplateLoader {

    private final ProcessingEnvironment _procEnv;
    private final String _basePkgPath;

    public CompileTimeTemplateLoader(
            final ProcessingEnvironment processingEnvironment,
            final String basePackagePath
    ) {
        ArgumentChecker.required(processingEnvironment, "processingEnvironment");
        this._procEnv = processingEnvironment;
        this._basePkgPath = basePackagePath;
    }

    @Override
    public Object findTemplateSource(
            final String name
    ) throws IOException {
        ArgumentChecker.required(name, "name");
        FileObject fObj = this._procEnv.getFiler().getResource(
                StandardLocation.CLASS_PATH, this._basePkgPath, name);
        return fObj;
    }

    @Override
    public long getLastModified(
            final Object templateSource
    ) {
        if (! (templateSource instanceof FileObject)) {
            throw new KernelException(
                    "The input must be a FileObject - {}",
                    templateSource.getClass().getName());
        }
        FileObject fObj = (FileObject) templateSource;
        return fObj.getLastModified();
    }

    @Override
    public Reader getReader(
            final Object templateSource,
            final String encoding
    ) throws IOException {
        if (! (templateSource instanceof FileObject)) {
            throw new KernelException(
                    "The input must be a FileObject - {}",
                    templateSource.getClass().getName());
        }
        FileObject fObj = (FileObject) templateSource;
        return fObj.openReader(true);
    }

    @Override
    public void closeTemplateSource(
            final Object templateSource
    ) throws IOException {
        // Do nothing
    }
}
