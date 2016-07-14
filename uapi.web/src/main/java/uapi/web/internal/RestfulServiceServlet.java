/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import com.google.common.base.Strings;
import rx.Observable;
import uapi.KernelException;
import uapi.Type;
import uapi.config.annotation.Config;
import uapi.helper.CollectionHelper;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.*;
import uapi.service.annotation.*;
import uapi.service.annotation.Optional;
import uapi.service.web.*;
import uapi.web.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Generic web service url mapping: /[prefix]/[web service name]/[uri params]?[query strings]
 */
@Service(MappableHttpServlet.class)
public class RestfulServiceServlet extends MappableHttpServlet {

    private static final String SEPARATOR_URI_QUERY_PARAM       = "\\?";
    private static final char SEPARATOR_QUERY_PARAM_KEY_VALUE   = '=';
    private static final String PARAM_INTERFACE                 = "interface";

    @Config(path=IWebConfigurableKey.RESTFUL_URI_PREFIX, optional=true)
    String _context = Constant.DEF_RESTFUL_URI_PREFIX;

    @Config(path=IWebConfigurableKey.RESTFUL_CODEC)
    String _codecName;

    @Config(path=IWebConfigurableKey.SERVER_HTTP_HOST)
    String _host;

    @Config(path=IWebConfigurableKey.SERVER_HTTP_PORT)
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

//    @Inject
//    IRegistry _registry;

    @Inject
    @Optional
    Map<String, IServiceRegister> _svcRegisters = new HashMap<>();

    @Init
    public void init() { }

    @Override
    public String getPathPattern() {
        return "/" + this._context + "/*";
    }

    /**
     * The response of restful interface query is:
     *  {
     *      response-code: [success or fail]
     *      response-message: [message]
     *      data: {...}
     *  }
     *  data -> {
     *      interface-id: 'xxx',
     *      communicator-name: 'xxx',
     *      service-metas: [{
     *          name: 'xxx',
     *          return-type-name: 'xxx',
     *          uri: 'xxx',
     *          method: 'GET',
     *          codec-name: 'JSON',
     *          communication: 'RESTful',
     *          argument-metas: [{
     *              type-name: 'xxx',
     *              from: 'xxx',
     *              index: 2,
     *              name: 'xxx'
     *          }, ...]
     *      }, ...]
     *  }
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws ServletException, IOException {
        UriInfo uriInfo = parseUri(request);
        if (Strings.isNullOrEmpty(uriInfo.serviceName)) {
            // Handle restful interface query
            handleDiscoverRequest(request, response, uriInfo);
        } else {
            handleRequest(request, response, HttpMethod.GET, uriInfo);
        }
    }

    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws ServletException, IOException {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, HttpMethod.POST, uriInfo);
    }

    @Override
    protected void doPut(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws ServletException, IOException {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, HttpMethod.PUT, uriInfo);
    }

    @Override
    protected void doDelete(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws ServletException, IOException {
        UriInfo uriInfo = parseUri(request);
        handleRequest(request, response, HttpMethod.DELETE, uriInfo);
    }

    private void handleDiscoverRequest(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final UriInfo uriInfo
    ) throws ServletException, IOException {
        String[] intfNames = uriInfo.queryParams.get(PARAM_INTERFACE);
        if (intfNames == null || intfNames.length != 1) {
            throw new KernelException("Only allow query 1 restful interface - ", CollectionHelper.asString(intfNames));
        }
        String intfName = intfNames[0];
        if (Strings.isNullOrEmpty(intfName)) {
            throw new KernelException("The queried restful service type can't be empty");
        }
        List<IRestfulInterface> matchedIntfs = Looper.from(this._restIntfs)
                .filter(restIntf -> restIntf.getInterfaceId().equals(intfName))
                .toList();
        ServiceDiscoveryResponse resp = new ServiceDiscoveryResponse();
        if (matchedIntfs.size() != 1) {
            resp.code = CommonResponseCode.FAILURE;
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
                        svcMeta.methods = entry.getValue();//.toArray(new HttpMethod[entry.getValue().size()]);
                        List<ArgumentMeta> argMappings = svcInfo.getArgumentMappings();
                        svcMeta.argumentMetas = new ServiceDiscoveryResponse.ArgumentMeta[argMappings.size()];
                        Looper.from(argMappings)
                                .map(argMapping -> (ArgumentMapping) argMapping)
                                .foreachWithIndex((argIdx, argMapping) -> {
                                    ServiceDiscoveryResponse.ArgumentMeta argMeta = new ServiceDiscoveryResponse.ArgumentMeta();
                                    if (argMapping instanceof NamedArgumentMapping) {
                                        NamedArgumentMapping nArgMapping = (NamedArgumentMapping) argMapping;
                                        argMeta.name = nArgMapping.getName();
                                    } else if (argMapping instanceof IndexedArgumentMapping) {
                                        IndexedArgumentMapping iArgMapping = (IndexedArgumentMapping) argMapping;
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
            throw new KernelException("The response codec was not found - {}", this._codecName);
        }
        response.getWriter().print(codec.decode(resp, ServiceDiscoveryResponse.class));
        response.flushBuffer();
    }

    private void handleRequest(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final HttpMethod method,
            final UriInfo uriInfo
    ) throws ServletException, IOException {
        String svcName = uriInfo.serviceName;
        IRestfulService matchedWebSvc = this._restSvcs.get(svcName);
        if (matchedWebSvc == null) {
            throw new KernelException("No web service is matched name {}", svcName);
        }
        ArgumentMapping[] argMetas = matchedWebSvc.getMethodArgumentsInfo(method);
        List<Object> argValues = new ArrayList<>();
        Observable.from(argMetas)
                .subscribe(argMeta -> {
                    ArgumentFrom from = argMeta.getFrom();
                    String value = null;
                    if (from == ArgumentFrom.Header) {
                        value = request.getHeader(((NamedArgumentMapping) argMeta).getName());
                        } else if (from == ArgumentFrom.Uri) {
                        value = uriInfo.uriParams.get(((IndexedArgumentMapping) argMeta).getIndex());
                    } else if (from == ArgumentFrom.Param) {
                        value = uriInfo.queryParams.get(((NamedArgumentMapping) argMeta).getName())[0];
                    } else {
                        throw new KernelException("Unsupported from indication {}", from);
                    }
                    argValues.add(parseValue(value, argMeta.getType()));
                }, this._logger::error);
        Object result = matchedWebSvc.invoke(method, argValues);
        IStringCodec codec = this._codecs.get(this._codecName);
        if (codec == null) {
            throw new KernelException("The response codec was not found - {}", this._codecName);
        }
        Class<?> type = this._typeMapper.getType(matchedWebSvc.getReturnTypeName(method));
        if (type == null) {
            throw new KernelException("Unsupported typ - {}", matchedWebSvc.getReturnTypeName(method));
        }
        response.getWriter().print(codec.decode(result, type));
        response.flushBuffer();
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

    private UriInfo parseUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String urlPrefix = "/" + this._context;
        if (uri.indexOf(urlPrefix) != 0) {
            throw new KernelException("The requested URI {} is not prefixed by {}", uri, urlPrefix);
        }
        String[] pathAndQuery = uri.split(SEPARATOR_URI_QUERY_PARAM);
        String path = pathAndQuery[0];
        String query = pathAndQuery.length >= 2 ? pathAndQuery[1] : null;

        UriInfo uriInfo = new UriInfo();
        StringBuilder buffer = new StringBuilder();
        for (int i = urlPrefix.length(); i < path.length(); i++) {
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
        if (query != null) {
            String key = null;
            String value = null;
            for (int i = 0; i < query.length(); i++) {
                char c = query.charAt(i);
                switch (c) {
                    case SEPARATOR_QUERY_PARAM_KEY_VALUE:
                        key = buffer.toString();
                        buffer.delete(0, buffer.length());
                        break;
//                    case SEPARATOR_QUERY_PARAM:
//                        value = buffer.toString();
//                        if (ArgumentChecker.isEmpty(key) || ArgumentChecker.isEmpty(value)) {
//                            throw new KernelException("The query string of uri {} is invalid: empty key or value");
//                        }
//                        uriInfo.queryParams.put(key, value);
//                        key = null;
//                        value = null;
//                        buffer.delete(0, buffer.length());
//                        break;
                    default:
                        buffer.append(c);
                }
            }
        }
        Observable.from(request.getParameterMap().entrySet())
                .subscribe(entry -> uriInfo.queryParams.put(entry.getKey(), entry.getValue()));

        return uriInfo;
    }

    private final class UriInfo {

        private String serviceName;
        private List<String> uriParams = new ArrayList<>();
        private Map<String, String[]> queryParams = new HashMap<>();
    }
}
