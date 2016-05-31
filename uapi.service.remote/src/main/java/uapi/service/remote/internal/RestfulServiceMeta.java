package uapi.service.remote.internal;

import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
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
    private String _format;

    public RestfulServiceMeta(
            final String name,
            final String valueParserName,
            final List<ArgumentMapping> argMappings,
            final String uri,
            final HttpMethod httpMethod,
            final String format
    ) {
        super(name, valueParserName, argMappings);
        ArgumentChecker.required(uri, "uri");
        ArgumentChecker.required(httpMethod, "httpMethod");
        ArgumentChecker.required(format, "format");
        this._uri = uri;
        this._method = httpMethod;
    }

    public String getUri() {
        return this._uri;
    }

    public HttpMethod getMethod() {
        return this._method;
    }

    public String getFormat() {
        return this._format;
    }

    public String getCommunicatorName() {
        return RestfulCommunicator.id;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("RestfulServiceMeta[{},uri={},method={},format{}]",
                super.toString(), this._uri, this._method, this._format);
    }
}
