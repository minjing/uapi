package uapi.web.internal;

import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.MappableHttpServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xquan on 4/28/2016.
 */
@Service(MappableHttpServlet.class)
public class WebServiceServlet extends MappableHttpServlet {

    private static final String URL_MAPPING = "/ws/*";


    @Override
    public String getPathPattern() {
        return URL_MAPPING;
    }

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws ServletException, IOException {

    }
}
