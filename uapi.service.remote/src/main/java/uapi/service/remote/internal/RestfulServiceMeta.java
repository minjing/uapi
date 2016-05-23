package uapi.service.remote.internal;

import uapi.helper.ArgumentChecker;
import uapi.service.remote.ServiceMeta;
import uapi.service.web.ArgumentMapping;
import uapi.service.web.HttpMethod;

import java.util.List;

/**
 * Restful service meta
 */
public class RestfulServiceMeta extends ServiceMeta {

    private String _uri;
    private HttpMethod _method;

    public RestfulServiceMeta(
            final String name,
            final List<ArgumentMapping> argMappings,
            final String uri,
            final HttpMethod httpMethod) {
        super(name, argMappings);
        ArgumentChecker.required(uri, "uri");
        ArgumentChecker.required(httpMethod, "httpMethod");
        this._uri = uri;
        this._method = httpMethod;
    }

    public String getUri() {
        return this._uri;
    }

    public HttpMethod getMethod() {
        return this._method;
    }

    @Override
    public String getCommunicatorName() {
        return RestfulCommunicator.id;
    }
}
