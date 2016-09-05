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
import uapi.Type;
import uapi.config.annotation.Config;
import uapi.helper.CollectionHelper;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.*;
import uapi.service.IStringCodec;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Optional;
import uapi.service.annotation.Service;
import uapi.web.http.*;
import uapi.web.restful.*;
import uapi.web.restful.ArgumentFrom;
import uapi.web.restful.ArgumentMapping;
import uapi.web.restful.Constant;
import uapi.web.restful.IndexedArgumentMapping;
import uapi.web.restful.NamedArgumentMapping;

import java.util.*;

/**
 * Profile handle Restful http request
 */
@Service(IHttpHandler.class)
public class RestfulHttpHandler implements IHttpHandler {

    private static final String SEPARATOR_URI_QUERY_PARAM       = "\\?";
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

    /**
     * Query data.
     * Success: return 200(OK) and attach the response in the body
     * FAILURE: return 404(Not Found) if no such data can be returned
     *
     * @param request
     * @param response
     */
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

    /**
     * Update whole data.
     * Success: return 200(OK)
     * FAILURE: return 404(Not Found) if no such data can be updated
     *
     * @param request
     * @param response
     */
    @Override
    public void put(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    /**
     * Create data.
     * Success: return 200(OK)
     * FAILURE: return 400(Bad Request) if the modified data is not valid
     *          return 409(Conflict) if created data is conflict with existing data
     *
     * @param request
     * @param response
     */
    @Override
    public void post(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    /**
     * Update party of data.
     * Success: return 200(OK)
     * Failure: return 400(Bad Request) if the modified data is not valid
     *          return 404(Not Found) if no such data can be updated
     *
     * @param request
     * @param response
     */
    @Override
    public void patch(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    /**
     * Delete data.
     * Success: return 200(OK)
     * Failure: return 404(Not Found) if data can be found
     *
     * @param request
     * @param response
     */
    @Override
    public void delete(IHttpRequest request, IHttpResponse response) {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, uriInfo);
    }

    private void handleRequest(IHttpRequest request, IHttpResponse response, UriInfo uriInfo) {
        String svcName = uriInfo.serviceName;
        IRestfulService matchedSvc = this._restSvcs.get(svcName);
        if (matchedSvc == null) {
            throw new BadRequestException("No restful service is matched name with {}", svcName);
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
                        String paramName = ((NamedArgumentMapping) argMapping).getName();
                        List<String> params = request.params().get(paramName);
                        if (params == null || params.size() == 0) {
                            throw new BadRequestException(paramName);
                        }
                        value = params.get(0);
                    } else {
                        throw new InternalServerException("Unsupported from indication {}", from);
                    }
                    argValues.add(parseValue(value, argMapping.getType()));
                });
        Object result = matchedSvc.invoke(request.method(), argValues);
        IStringCodec codec = this._codecs.get(this._codecName);
        if (codec == null) {
            throw new InternalServerException("The response codec was not found - {}", this._codecName);
        }
        Class<?> type = this._typeMapper.getType(matchedSvc.getReturnTypeName(request.method()));
        if (type == null) {
            throw new InternalServerException("Unsupported typ - {}", matchedSvc.getReturnTypeName(request.method()));
        }
        String resTxt = codec.decode(result, type);
        this._logger.debug("Response text -> {}", resTxt);
        response.write(resTxt);
//        response.flush();
    }

    private void handleDiscoverRequest(IHttpRequest request, IHttpResponse response, UriInfo uriInfo) {
        List<String> intfNames = request.params().get(PARAM_INTERFACE);
        if (intfNames == null || intfNames.size() != 1) {
            throw new BadRequestException("Only allow query 1 restful interface - ", CollectionHelper.asString(intfNames));
        }
        String intfName = intfNames.get(0);
        if (Strings.isNullOrEmpty(intfName)) {
            throw new BadRequestException("The queried restful service type can't be empty");
        }
        List<IRestfulInterface> matchedIntfs = Looper.from(this._restIntfs)
                .filter(restIntf -> restIntf.getInterfaceId().equals(intfName))
                .toList();
        ServiceDiscoveryResponse resp = new ServiceDiscoveryResponse();
        if (matchedIntfs.size() != 1) {
            throw new NotFoundException(intfName);
        } else {
            resp.code = CommonResponseCode.SUCCESS;
            IRestfulInterface restfulIntf = matchedIntfs.get(0);
            resp.data = new ServiceDiscoveryResponse.Data();
            resp.data.communication = Constants.COMMUNICATION_RESTFUL;
            resp.data.interfaceId = restfulIntf.getInterfaceId();
            Map<ServiceMeta, List<HttpMethod>> methodHttpMethodMappings = restfulIntf.getMethodHttpMethodInfos();
            resp.data.serviceMetas = new ArrayList<>();
            Looper.from(methodHttpMethodMappings.entrySet())
                    .foreachWithIndex((index, entry) -> {
                        ServiceMeta svcInfo = entry.getKey();
                        ServiceDiscoveryResponse.ServiceMeta svcMeta = new ServiceDiscoveryResponse.ServiceMeta();
                        svcMeta.id = svcInfo.getId();
                        svcMeta.name = svcInfo.getName();
                        svcMeta.returnTypeName = svcInfo.getReturnTypeName();
                        svcMeta.codec = this._codecName;
                        svcMeta.host = this._host;
                        svcMeta.port = this._port;
                        svcMeta.context = this._context;
                        svcMeta.methods = entry.getValue();
                        List<ArgumentMeta> argMappings = svcInfo.getArgumentMetas();
                        svcMeta.argumentMetas = new ServiceDiscoveryResponse.ArgumentMeta[argMappings.size()];
                        Looper.from(argMappings)
                                .map(argMapping -> (uapi.web.restful.ArgumentMapping) argMapping)
                                .foreachWithIndex((argIdx, argMapping) -> {
                                    ServiceDiscoveryResponse.ArgumentMeta argMeta = new ServiceDiscoveryResponse.ArgumentMeta();
                                    if (argMapping instanceof uapi.web.restful.NamedArgumentMapping) {
                                        uapi.web.restful.NamedArgumentMapping nArgMapping = (uapi.web.restful.NamedArgumentMapping) argMapping;
                                        argMeta.name = nArgMapping.getName();
                                    } else if (argMapping instanceof uapi.web.restful.IndexedArgumentMapping) {
                                        uapi.web.restful.IndexedArgumentMapping iArgMapping = (uapi.web.restful.IndexedArgumentMapping) argMapping;
                                        argMeta.index = iArgMapping.getIndex();
                                    }
                                    argMeta.from = argMapping.getFrom();
                                    argMeta.typeName = argMapping.getType();
                                    svcMeta.argumentMetas[argIdx] = argMeta;
                                });
                        resp.data.serviceMetas.add(svcMeta);
                    });
        }
        IStringCodec codec = this._codecs.get(this._codecName);
        if (codec == null) {
            throw new InternalServerException("The response codec was not found - {}", this._codecName);
        }
        String resTxt = codec.decode(resp, ServiceDiscoveryResponse.class);
        this._logger.debug("Response text -> {}", resTxt);
        response.write(resTxt);
//        response.flush();
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
        throw new InternalServerException("Unknown type name {}", type);
    }

    private UriInfo parseUri(IHttpRequest request) {
        String uri = request.uri();
        if (uri.indexOf(this._context) != 0) {
            throw new BadRequestException("The request URI {} is not prefixed by {}", uri, this._context);
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
    }
}
