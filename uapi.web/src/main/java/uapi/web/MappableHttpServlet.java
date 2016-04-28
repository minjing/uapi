package uapi.web;

import javax.servlet.http.HttpServlet;

/**
 * Created by xquan on 4/28/2016.
 */
public abstract class MappableHttpServlet extends HttpServlet {

    public abstract String getPathPattern();
}
