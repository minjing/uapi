package uapi.annotation.internal;

import freemarker.cache.TemplateLoader;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;

import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Reader;

/**
 * The template loader for compiling source time
 */
public class CompileTimeTemplateLoader implements TemplateLoader {

    private final BuilderContext _builderCtx;
    private final String _basePkgPath;

    public CompileTimeTemplateLoader(
            final BuilderContext builderContext,
            final String basePackagePath
    ) {
        ArgumentChecker.notNull(builderContext, "builderContext");
        this._builderCtx = builderContext;
        this._basePkgPath = basePackagePath;
    }

    @Override
    public Object findTemplateSource(
            final String name
    ) throws IOException {
        ArgumentChecker.required(name, "name");
        FileObject fObj = this._builderCtx.getFiler().getResource(
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
