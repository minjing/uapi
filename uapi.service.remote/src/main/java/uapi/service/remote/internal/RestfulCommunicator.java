/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal;

import okhttp3.*;
import okhttp3.Response;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.Triple;
import uapi.log.ILogger;
import uapi.service.*;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.web.http.HttpMethod;
import uapi.web.http.HttpResponseStatus;
import uapi.web.restful.ArgumentFrom;
import uapi.web.restful.ArgumentMapping;
import uapi.web.restful.IndexedArgumentMapping;
import uapi.web.restful.NamedArgumentMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The driver use restful and json encode to invoke remote service
 */
@Service(ICommunicator.class)
public class RestfulCommunicator implements ICommunicator {

    public static final String id = "Restful";

    @Inject
    ILogger _logger;

    @Inject
    Map<String, IStringCodec> _codecs = new HashMap<>();

    @Inject
    TypeMapper _typeMapper;

    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object request(ServiceMeta serviceMeta, Object... args) {
        ArgumentChecker.required(serviceMeta, "serviceMeta");
        ArgumentChecker.required(args, "args");

        if (! (serviceMeta instanceof RestfulServiceMeta)) {
            throw new KernelException("The {} can't handle : {}", this.getClass().getName(), serviceMeta.getClass().getName());
        }
        RestfulServiceMeta restfulSvcMeta = (RestfulServiceMeta) serviceMeta;
        List<ArgumentMeta> argMappings = restfulSvcMeta.getArgumentMetas();
        if (argMappings.size() != args.length) {
            throw new KernelException("Found unmatched service {} argument size {}, expect {}",
                    serviceMeta.getName(), argMappings.size(), args.length);
        }

        String format = restfulSvcMeta.getCodec();
        HttpMethod reqMethod = restfulSvcMeta.getMethods().get(0);
        List<Triple<ArgumentMapping, String, Object>> uriArgs = new ArrayList<>();
        Map<String, Triple<ArgumentMapping, String, Object>> paramArgs = new HashMap<>();
        Map<String, Triple<ArgumentMapping, String, Object>> headerArgs = new HashMap<>();
        for (int i = 0; i < argMappings.size(); i++) {
            ArgumentMapping argMapping = (ArgumentMapping) argMappings.get(i);
            if (argMapping instanceof IndexedArgumentMapping) {
                int index = ((IndexedArgumentMapping) argMapping).getIndex();
                if (index >= args.length) {
                    throw new KernelException("The service {} argument {}'s index {} is out of real arguments index {}",
                            restfulSvcMeta.getName(), i, index, args.length - 1);
                }
                uriArgs.add(index, new Triple<>(argMapping, format, args[i]));
            } else if (argMapping instanceof NamedArgumentMapping) {
                String argName = ((NamedArgumentMapping) argMapping).getName();
                if (argMapping.getFrom() == ArgumentFrom.Param) {
                    paramArgs.put(argName, new Triple<>(argMapping, format, args[i]));
                } else if (argMapping.getFrom() == ArgumentFrom.Header) {
                    headerArgs.put(argName, new Triple<>(argMapping, format, args[i]));
                }
            } else {
                throw new KernelException("Unsupported argument mapping type {}", argMapping.getClass().getName());
            }
        }

        // Ensure urlArgs must be sequent
        if (CollectionHelper.hasNull(uriArgs)) {
            throw new KernelException("The uri arguments of service {} has empty argument: {}",
                    restfulSvcMeta.getName(), CollectionHelper.asString(uriArgs));
        }

        // Construct request uri from uriArgs
        Request.Builder reqBuild = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(restfulSvcMeta.getUri()).newBuilder();
        Observable.from(uriArgs).subscribe(uriArg -> urlBuilder.addPathSegment(decodeValue(uriArg)));

        // Construct request parameters
        if (headerArgs.size() > 0) {
            Observable.from(headerArgs.entrySet())
                    .subscribe(entry -> reqBuild.addHeader(
                            entry.getKey(), decodeValue(entry.getValue())));
        }
        if (reqMethod == HttpMethod.GET) {
            if (paramArgs.size() > 0) {
                Observable.from(paramArgs.entrySet())
                        .subscribe(entry -> urlBuilder.addQueryParameter(
                                entry.getKey(), decodeValue(entry.getValue())));
            }
            reqBuild.url(urlBuilder.build());
        } else if (reqMethod == HttpMethod.POST) {
            reqBuild.url(urlBuilder.build());
            if (paramArgs.size() > 0) {
                FormBody.Builder formBody = new FormBody.Builder();
                Observable.from(paramArgs.entrySet())
                        .subscribe(entry -> formBody.add(
                                entry.getKey(), decodeValue(entry.getValue())));
                reqBuild.post(formBody.build());
            }
        } else {
            throw new KernelException("Unsupported HTTP method {}", reqMethod);
        }

        Request request = reqBuild.build();
        this._logger.info("Request url is {}", request.toString());
        try {
            Response response = httpClient.newCall(request).execute();
            if (reqMethod == HttpMethod.POST && response.code() != HttpResponseStatus.CREATED.getCode()) {
                throw new KernelException("POST for {} got a non {} response: {}",
                        request.url(), HttpResponseStatus.CREATED.getCode(), response.body().string());
            } else if (response.code() != HttpResponseStatus.OK.getCode()) {
                throw new KernelException("Request for {} got a non {} response: {}",
                        request.url(), HttpResponseStatus.OK.getCode(), response.body().string());
            }
            return decodeResponse(new Triple<>(
                    restfulSvcMeta.getReturnTypeName(), restfulSvcMeta.getCodec(), response.body().string()));
        } catch (IOException ex) {
            throw new KernelException(ex, "Encounter an error when invoke service {}", restfulSvcMeta.toString());
        }
    }

    /**
     * Encode argument value to a formatted string
     *
     * @param   valueInfo
     *          The value is a Triple instance which is composed with argument mapping information,
     *          encode name and argument value
     * @return  Formatted string
     */
    private String decodeValue(Triple<ArgumentMapping, String, Object> valueInfo) {
        String codecName = valueInfo.getCenterValue();
        IStringCodec codec = this._codecs.get(codecName);
        if (codec == null) {
            throw new KernelException("No codec was mapped to name {}", codecName);
        }
        String typeName = valueInfo.getLeftValue().getType();
        Class<?> type = this._typeMapper.getType(typeName);
        if (type == null) {
            throw new KernelException("No type was mapped to name {}", typeName);
        }
        return codec.decode(valueInfo.getRightValue(), type);
//        IStringResolver resolver = this._resolvers.get(typeName);
//        if (resolver == null) {
//            throw new KernelException("No resolver was mapped to name {}", typeName);
//        }
//        return resolver.decode(valueInfo.getRightValue(), valueInfo.getCenterValue());

    }

    /**
     * Decode response text to specific object
     *
     * @param   responseInfo
     *          The responseIfo is a Tuple instance which is composed with return type name,
     *          encode name and response text
     * @return  The decoded object
     */
    private Object decodeResponse(Triple<String, String, String> responseInfo) {
        ArgumentChecker.required(responseInfo, "responseIfo");
        String rtnTypeName = responseInfo.getLeftValue();
        String format = responseInfo.getCenterValue();
        String respText = responseInfo.getRightValue();
        IStringCodec codec = this._codecs.get(format);
        if (codec == null) {
            throw new KernelException("Not type was mapped to name {}", rtnTypeName);
        }
        Class<?> type = this._typeMapper.getType(rtnTypeName);
        return codec.encode(respText, type);
//        IStringResolver resolver = this._resolvers.get(rtnTypeName);
//        if (resolver ==  null) {
//            throw new KernelException("No resolver was mapped to name {}", rtnTypeName);
//        }
//        return resolver.encode(respText, format);
    }
}
