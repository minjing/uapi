package uapi.web.internal;

import uapi.config.annotation.Config;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.web.IWebConfigurableKey;
import uapi.web.MappableHttpServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Generic web service url mapping: /[prefix]/[web service name]
 */
@Service(MappableHttpServlet.class)
public class WebServiceServlet extends MappableHttpServlet {

    private static final String DEFAULT_URI_PATTERN = "/ws/*";

    @Config(path=IWebConfigurableKey.WS_URI_PATTERN)
    String _uriPattern = DEFAULT_URI_PATTERN;

    @Inject
    IRegistry _registry;

    @Override
    public String getPathPattern() {
        return this._uriPattern;
    }

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws ServletException, IOException {

    }
}
