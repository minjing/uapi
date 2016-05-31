/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web;

import uapi.IIdentifiable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xquan on 4/28/2016.
 */
public interface IResponseWriter<T> extends IIdentifiable<String> {

    /**
     * Write result to http response.
     * @param   result
     *          The object will be encoded and output to response
     * @param   response
     *          The http response object
     */
    void write(T result, HttpServletResponse response) throws IOException;
}
