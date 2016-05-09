package uapi.web;

import uapi.config.IConfigurableKey;

/**
 * The interface hold all configurable keys for web
 */
public interface IWebConfigurableKey extends IConfigurableKey {

    /**
     * Below configurations are used in JetterHttpServer
     */
    String SERVER_HTTP_HOST         = "server.http.host";
    String SERVER_HTTP_PORT         = "server.http.port";
    String SERVER_HTTP_IDLE_TIMEOUT = "server.http.idle-timeout";

    /**
     * Below configurations are used in RestfulServiceServlet
     */
    String RESTFUL_URI_PATTERN      = "restful.url-pattern";
    String RESTFUL_ENCODER          = "restful.encoder";
}
