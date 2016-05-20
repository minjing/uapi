package uapi.service.remote;

import uapi.config.IConfigurableKey;

/**
 * The interface hold all configurable keys for remote service module
 */
public interface IRemoteServiceConfigurableKey extends IConfigurableKey {

    /**
     * Directly remote service keys
     */
    String DIRECT_SVC_HOST      = "service.remote.direct.host";
    String DIRECT_SVC_PORT      = "service.remote.direct.port";
    String DIRECT_SVC_DRIVER    = "service.remote.direct.driver";

    String RESTFUL_HTTP_TYPE    = "service.remote.driver.restful.http_type";
    String RESTFUL_URI_PRIFIX   = "service.remote.driver.restful.uri_prefix";

    String LOADER_DISCOVER      = "service.remote.loader.discover";

    String DISCOVER_COMM        = "service.remote.discover.communicator";
    String DISCOVER_HOST        = "service.remote.discover.host";
    String DISCOVER_PORT        = "service.remote.discover.port";
    String DISCOVER_CALL_TYPE   = "service.remote.discover.call_type";
    String DISCOVER_URI_PREFIX  = "service.remote.discover.uri_prefix";
}
