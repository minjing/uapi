package uapi.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper for java.lang.Exception
 */
public final class ExceptionHelper {

    public static String getStackString(Exception ex) {
        ArgumentChecker.required(ex, "ex");
        StringWriter strWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(strWriter));
        return strWriter.toString();
    }

    private ExceptionHelper() { }
}
