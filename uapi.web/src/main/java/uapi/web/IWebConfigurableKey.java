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
     * Below configurations are used in WebServiceServlet
     */
    String WS_URI_PATTERN           = "ws.url-pattern";
}
