package uapi.web;

import javax.servlet.http.HttpServlet;

/**
 * A IServletInfo hold information for specific servlet
 */
public interface IServletInfo {

    String getPathPattern();

    Class<? extends HttpServlet> getServletClass();
}
