package uapi.web.internal;

import com.fasterxml.jackson.jr.ob.JSON;
import uapi.web.IResponseWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xquan on 5/2/2016.
 */
public class JasonResponseWriter implements IResponseWriter<Object> {

    private static final String JASON   = "JASON";

    @Override
    public String getId() {
        return JASON;
    }

    @Override
    public void write(Object result, HttpServletResponse response
    ) throws IOException {
        JSON.std.write(result, response.getOutputStream());
    }
}
