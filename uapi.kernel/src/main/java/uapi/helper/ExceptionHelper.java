package uapi.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper for java.lang.Exception
 */
public final class ExceptionHelper {

    public static String getStackString(Throwable t) {
        ArgumentChecker.required(t, "t");
        StringWriter strWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(strWriter));
        return strWriter.toString();
    }

    private ExceptionHelper() { }
}
