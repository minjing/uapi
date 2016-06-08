/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import com.fasterxml.jackson.jr.ob.JSON;
import uapi.service.annotation.Service;
import uapi.web.IResponseWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Output object to JSON encode to the http response
 */
@Service(IResponseWriter.class)
public class JasonResponseWriter implements IResponseWriter<Object> {

    private static final String NAME_JSON   = "JSON";

    @Override
    public String getId() {
        return NAME_JSON;
    }

    @Override
    public void write(Object result, HttpServletResponse response
    ) throws IOException {
        JSON.std.write(result, response.getOutputStream());
    }
}
