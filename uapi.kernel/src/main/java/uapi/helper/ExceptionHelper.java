/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
