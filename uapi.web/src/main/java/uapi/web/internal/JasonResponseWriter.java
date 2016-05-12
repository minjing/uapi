package uapi.web.internal;

import com.fasterxml.jackson.jr.ob.JSON;
import uapi.service.annotation.Service;
import uapi.web.IResponseWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Output object to JSON format to the http response
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
