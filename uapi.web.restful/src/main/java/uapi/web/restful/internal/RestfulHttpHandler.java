/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful.internal;

import com.google.common.base.Strings;
import uapi.KernelException;
import uapi.Type;
import uapi.config.annotation.Config;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.IStringCodec;
import uapi.service.TypeMapper;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;
import uapi.service.annotation.Service;
import uapi.web.http.IHttpHandler;
import uapi.web.http.IHttpRequest;
import uapi.web.http.IHttpResponse;
import uapi.web.restful.*;

import java.util.*;

/**
 * Profile handle Restful http request
 */
@Service(IHttpHandler.class)
public class RestfulHttpHandler implements IHttpHandler {

    private static final String SEPARATOR_URI_QUERY_PARAM       = "\\?";
    private static final char SEPARATOR_QUERY_PARAM_KEY_VALUE   = '=';
    private static final String PARAM_INTERFACE                 = "interface";

    @Config(path=IRestfulConfigurableKey.RESTFUL_URI_PREFIX, optional=true)
    String _context = Constant.DEF_RESTFUL_URI_PREFIX;

    @Config(path=IRestfulConfigurableKey.RESTFUL_CODEC)
    String _codecName;

    @Config(path=IRestfulConfigurableKey.SERVER_HTTP_HOST)
    String _host;

    @Config(path=IRestfulConfigurableKey.SERVER_HTTP_PORT)
    int _port;

    @Inject
    ILogger _logger;

    @Inject
    Map<String, IRestfulService> _restSvcs = new HashMap<>();

    @Inject
    @Optional
    List<IRestfulInterface> _restIntfs = new LinkedList<>();

    @Inject
    Map<String, IStringCodec> _codecs = new HashMap<>();

    @Inject
    TypeMapper _typeMapper;

    @Override
    public String getUriMapping() {
        return this._context;
    }

    @Override
    public void get(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        if (Strings.isNullOrEmpty(uriInfo.serviceName)) {
            // Handle restful interface query
            handleDiscoverRequest(request, response, uriInfo);
        } else {
            handleRequest(request, response, uriInfo);
        }
    }

    @Override
    public void put(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    @Override
    public void post(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    @Override
    public void delete(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    private void handleRequest(IHttpRequest request, IHttpResponse response, UriInfo uriInfo) {
        String svcName = uriInfo.serviceName;
        IRestfulService matchedSvc = this._restSvcs.get(svcName);
        if (matchedSvc == null) {
            throw new KernelException("No restful service is matched name with {}", svcName);
        }
        ArgumentMapping[] argMappings = matchedSvc.getMethodArgumentsInfo(request.method());
        List<Object> argValues = new ArrayList<>();
        Looper.from(argMappings)
                .foreach(argMapping -> {
                    ArgumentFrom from = argMapping.getFrom();
                    String value = null;
                    if (from == ArgumentFrom.Header) {
                        value = request.headers().get(((NamedArgumentMapping) argMapping).getName());
                    } else if (from == ArgumentFrom.Uri) {
                        value = uriInfo.uriParams.get(((IndexedArgumentMapping) argMapping).getIndex());
                    } else if (from == ArgumentFrom.Param) {
                        value = request.params().get(((NamedArgumentMapping) argMapping).getName()).get(0);
                    } else {
                        throw new KernelException("Unsupported from indication {}", from);
                    }
                    argValues.add(parseValue(value, argMapping.getType()));
                });
        Object result = matchedSvc.invoke(request.method(), argValues);
        IStringCodec codec = this._codecs.get(this._codecName);
        if (codec == null) {
            throw new KernelException("The response codec was not found - {}", this._codecName);
        }
        Class<?> type = this._typeMapper.getType(matchedSvc.getReturnTypeName(request.method()));
        if (type == null) {
            throw new KernelException("Unsupported typ - {}", matchedSvc.getReturnTypeName(request.method()));
        }
        response.write(codec.decode(result, type));
        response.flush();
    }

    private void handleDiscoverRequest(IHttpRequest request, IHttpResponse response, UriInfo uriInfo) {
        // TODO
    }

    private Object parseValue(String value, String type) {
        if (Type.Q_STRING.equals(type)) {
            return value;
        }
        if (Type.Q_BOOLEAN.equals(type)) {
            return Boolean.parseBoolean(value);
        }
        if (Type.Q_INTEGER.equals(type)) {
            return Integer.parseInt(value);
        }
        if (Type.Q_LONG.equals(type)) {
            return Long.parseLong(value);
        }
        if (Type.Q_FLOAT.equals(type)) {
            return Float.parseFloat(value);
        }
        if (Type.Q_DOUBLE.equals(type)) {
            return Double.parseDouble(value);
        }
        throw new KernelException("Unknown type name {}", type);
    }

    private UriInfo parseUri(IHttpRequest request) {
        String uri = request.uri();
        if (uri.indexOf(this._context) != 0) {
            throw new KernelException("The request URI {} is not prefixed by {}", uri, this._context);
        }
        String[] pathAndQuery = uri.split(SEPARATOR_URI_QUERY_PARAM);

        // parse path
        String path = pathAndQuery[0];
        UriInfo uriInfo = new UriInfo();
        StringBuilder buffer = new StringBuilder();
        for (int i = this._context.length(); i < path.length(); i++) {
            char c = path.charAt(i);
            switch (c) {
                case '/':
                    if (buffer.length() > 0) {
                        if (uriInfo.serviceName == null) {
                            uriInfo.serviceName = buffer.toString();
                        } else {
                            uriInfo.uriParams.add(buffer.toString());
                        }
                        buffer.delete(0, buffer.length());
                    }
                    break;
                default:
                    buffer.append(c);
            }
        }
        if (buffer.length() > 0) {
            if (uriInfo.serviceName == null) {
                uriInfo.serviceName = buffer.toString();
            } else {
                uriInfo.uriParams.add(buffer.toString());
            }
            buffer.delete(0, buffer.length());
        }

        return uriInfo;
    }

    private final class UriInfo {

        private String serviceName;
        private List<String> uriParams = new ArrayList<>();
//        private Map<String, String[]> queryParams = new HashMap<>();
    }
}
